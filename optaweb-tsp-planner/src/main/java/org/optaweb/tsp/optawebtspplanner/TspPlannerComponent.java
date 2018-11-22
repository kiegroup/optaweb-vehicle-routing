/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaweb.tsp.optawebtspplanner;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.graphhopper.GHRequest;
import com.graphhopper.reader.osm.GraphHopperOSM;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.util.PointList;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.event.BestSolutionChangedEvent;
import org.optaplanner.core.api.solver.event.SolverEventListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.examples.tsp.app.TspApp;
import org.optaplanner.examples.tsp.domain.Domicile;
import org.optaplanner.examples.tsp.domain.Standstill;
import org.optaplanner.examples.tsp.domain.TspSolution;
import org.optaplanner.examples.tsp.domain.Visit;
import org.optaplanner.examples.tsp.domain.location.Location;
import org.optaplanner.examples.tsp.domain.location.RoadLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class TspPlannerComponent implements SolverEventListener<TspSolution> {

    private static Logger logger = LoggerFactory.getLogger(TspMapController.class);

    private final SimpMessagingTemplate webSocket;
    private final PlaceRepository repository;
    private final Solver<TspSolution> solver;
    private final ScoreDirector<TspSolution> scoreDirector;
    private ThreadPoolTaskExecutor executor;
    private TspSolution tsp = new TspSolution();
    private List<Location> locations = new ArrayList<>();
    private GraphHopperOSM graphHopper;

    @Autowired
    public TspPlannerComponent(SimpMessagingTemplate webSocket,
                               PlaceRepository repository) {
        this.webSocket = webSocket;
        this.repository = repository;

        tsp.setLocationList(new ArrayList<>());
        tsp.setVisitList(new ArrayList<>());

        SolverFactory<TspSolution> sf = SolverFactory.createFromXmlResource(TspApp.SOLVER_CONFIG);
        sf.getSolverConfig().setDaemon(true);
        solver = sf.buildSolver();
        scoreDirector = solver.getScoreDirectorFactory().buildScoreDirector();
        solver.addEventListener(this);

        String osmPath = "local/belgium-latest.osm.pbf";
        if (!new File(osmPath).exists()) {
            throw new IllegalStateException("The osmPath (" + new File(osmPath).getAbsolutePath() + ") does not exist.\n" +
                    "Download the osm file from http://download.geofabrik.de/ first.");
        }
        graphHopper = (GraphHopperOSM) new GraphHopperOSM().forServer();
        graphHopper.setOSMFile(osmPath);
        graphHopper.setGraphHopperLocation("local/" + osmPath.replaceFirst(".*/(.*)\\.osm\\.pbf$", "$1-gh"));
        graphHopper.setEncodingManager(new EncodingManager("car"));
        logger.info("GraphHopper loading...");
        graphHopper.importOrLoad();
        logger.info("GraphHopper loaded.");
    }

    private static RoadLocation fromPlace(Place p) {
        return new RoadLocation(p.getId(), p.getLatitude().doubleValue(), p.getLongitude().doubleValue());
    }

    private static void addToRoute(Optional<Place> visit, List<Place> route) {
        if (!visit.isPresent()) {
            logger.info("Invalid solution, {} has been removed previously", visit);
            throw new IllegalStateException("Visit " + visit + " has been removed previously.");
        }
        visit.ifPresent(route::add);
    }

    private Optional<List<Place>> extractRoute(TspSolution tsp) {
        Map<Standstill, Visit> nextVisitMap = new LinkedHashMap<>();
        for (Visit visit : tsp.getVisitList()) {
            if (visit.getPreviousStandstill() != null) {
                nextVisitMap.put(visit.getPreviousStandstill(), visit);
            } else {
                logger.info("Ignoring a solution with an unconnected visit: {}", visit);
                return Optional.empty();
            }
        }

        // TODO race condition, if a rest thread deletes that location in the middle of this method happening on the solver thread
        // TODO make sure that location is still in the repository
        // TODO maybe repair the solution OR ignore if it's inconsistent (log WARNING)
        Domicile domicile = tsp.getDomicile();
        if (domicile == null) {
            return Optional.of(new ArrayList<>());
        }
        List<Place> route = new ArrayList<>();
        addToRoute(repository.findById(domicile.getLocation().getId()), route);
        for (Visit visit = nextVisitMap.get(domicile); visit != null; visit = nextVisitMap.get(visit)) {
            addToRoute(repository.findById(visit.getLocation().getId()), route);
        }
        return Optional.of(route);
    }

    private RouteMessage createResponse(TspSolution solution, List<Place> route) {
        List<List<Place>> segments = new ArrayList<>();
        for (int i = 1; i < route.size() + 1; i++) {
            GHRequest segmentRq = new GHRequest(
                    route.get(i - 1).getLatitude().doubleValue(),
                    route.get(i - 1).getLongitude().doubleValue(),
                    // "trick" to get N -> 0 distance at the end of the loop
                    route.get(i % route.size()).getLatitude().doubleValue(),
                    route.get(i % route.size()).getLongitude().doubleValue());
            PointList points = graphHopper.route(segmentRq).getBest().getPoints();
            List<Place> segment = StreamSupport.stream(points.spliterator(), false)
                    .map(ghPoint3D -> new Place(BigDecimal.valueOf(ghPoint3D.lat), BigDecimal.valueOf(ghPoint3D.lon)))
                    .collect(Collectors.toList());
            segments.add(segment);
        }
        String distanceString = solution.getDistanceString(new DecimalFormat("#,##0.00"));
        return new RouteMessage(distanceString, route, segments);
    }

    private void sendRoute(TspSolution solution) {
        extractRoute(solution).ifPresent(route -> {
            logger.info("New TSP with {} locations, {} visits, route: {}",
                    tsp.getLocationList().size(),
                    tsp.getVisitList().size(),
                    route);
            webSocket.convertAndSend("/topic/route", createResponse(solution, route));
        });
    }

    private void updateScore(TspSolution solution) {
        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
    }

    public RouteMessage getSolution() {
        List<Place> route = extractRoute(tsp)
                .orElseThrow(() -> new IllegalStateException("Best solution cannot have unconnected visits."));
        return createResponse(tsp, route);
    }

    @Override
    public void bestSolutionChanged(BestSolutionChangedEvent<TspSolution> bestSolutionChangedEvent) {
        if (!bestSolutionChangedEvent.isEveryProblemFactChangeProcessed()) {
            logger.info("Ignoring a new best solution that has some problem facts missing");
            return;
        }
        tsp = bestSolutionChangedEvent.getNewBestSolution();
        sendRoute(tsp);
    }

    public void addPlace(Place place) {
        RoadLocation location = fromPlace(place);
        Map<RoadLocation, Double> distanceMap = new HashMap<>();
        location.setTravelDistanceMap(distanceMap);
        for (Location other : locations) {
            RoadLocation toLocation = (RoadLocation) other;
            GHRequest there = new GHRequest(
                    location.getLatitude(),
                    location.getLongitude(),
                    toLocation.getLatitude(),
                    toLocation.getLongitude());
            distanceMap.put(toLocation, graphHopper.route(there).getBest().getDistance());
            GHRequest back = new GHRequest(
                    toLocation.getLatitude(),
                    toLocation.getLongitude(),
                    location.getLatitude(),
                    location.getLongitude());
            // TODO handle no route -> roll back the problem fact change
            toLocation.getTravelDistanceMap().put(location, graphHopper.route(back).getBest().getDistance());
        }
        locations.add(location);
        // Unfortunately can't start solver with an empty solution (see https://issues.jboss.org/browse/PLANNER-776)
        if (!solver.isSolving()) {
            List<Location> locationList = tsp.getLocationList();
            locationList.add(location);
            if (locationList.size() == 1) {
                Domicile domicile = new Domicile();
                domicile.setId(location.getId());
                domicile.setLocation(location);
                tsp.setDomicile(domicile);
                updateScore(tsp);
                sendRoute(tsp);
            } else if (locationList.size() == 2) {
                Visit visit = new Visit();
                visit.setId(location.getId());
                visit.setLocation(location);
                tsp.getVisitList().add(visit);
                executor = new ThreadPoolTaskExecutor();
                executor.initialize();
                executor.setWaitForTasksToCompleteOnShutdown(true);
                executor.setAwaitTerminationSeconds(5);
                executor.submit(() -> {
                    try {
                        solver.solve(tsp);
                    } catch (Exception e) {
                        logger.error("Solver error: ", e);
                    }
                });
            }
        } else {
            solver.addProblemFactChange(scoreDirector -> {
                TspSolution workingSolution = scoreDirector.getWorkingSolution();
                workingSolution.setLocationList(new ArrayList<>(workingSolution.getLocationList()));

                scoreDirector.beforeProblemFactAdded(location);
                workingSolution.getLocationList().add(location);
                scoreDirector.afterProblemFactAdded(location);

                Visit visit = new Visit();
                visit.setId(location.getId());
                visit.setLocation(location);

                scoreDirector.beforeEntityAdded(visit);
                workingSolution.getVisitList().add(visit);
                scoreDirector.afterEntityAdded(visit);

                scoreDirector.triggerVariableListeners();
            });
        }
    }

    public void removePlace(Place place) {
        Location location = fromPlace(place);
        locations.remove(location);
        if (!solver.isSolving()) {
            tsp.getLocationList().remove(0);
            tsp.setDomicile(null);
            updateScore(tsp);
            sendRoute(tsp);
        } else {
            if (tsp.getVisitList().size() == 1) {
                // domicile and 1 visit remaining
                solver.terminateEarly();
                executor.shutdown();
                tsp.getVisitList().remove(0);
                tsp.getLocationList().removeIf(l -> l.getId().equals(location.getId()));
                Location lastLocation = tsp.getLocationList().get(0);
                Domicile domicile = new Domicile();
                domicile.setId(lastLocation.getId());
                domicile.setLocation(lastLocation);
                tsp.setDomicile(domicile);
                updateScore(tsp);
                sendRoute(tsp);
            } else {
                if (tsp.getDomicile().getLocation().getId().equals(location.getId())) {
                    throw new UnsupportedOperationException("You can only remove domicile if it's the only location on map.");
                }
                solver.addProblemFactChanges(Arrays.asList(
                        scoreDirector -> {
                            TspSolution workingSolution = scoreDirector.getWorkingSolution();
                            Visit visit = workingSolution.getVisitList().stream()
                                    .filter(v -> v.getLocation().getId().equals(location.getId()))
                                    .findFirst()
                                    .orElseThrow(() -> new IllegalArgumentException(
                                            "Invalid request for removing visit at " + location));

                            // Remove the visit
                            scoreDirector.beforeEntityRemoved(visit);
                            if (!workingSolution.getVisitList().remove(visit)) {
                                throw new IllegalStateException("This is impossible.");
                            }
                            scoreDirector.afterEntityRemoved(visit);

                            // Fix the next visit and set its previousStandstill to the removed visit's previousStandstill
                            for (Visit nextVisit : workingSolution.getVisitList()) {
                                if (nextVisit.getPreviousStandstill().equals(visit)) {
                                    scoreDirector.beforeVariableChanged(nextVisit, "previousStandstill");
                                    nextVisit.setPreviousStandstill(visit.getPreviousStandstill());
                                    scoreDirector.afterVariableChanged(nextVisit, "previousStandstill");
                                    break;
                                }
                            }

                            scoreDirector.triggerVariableListeners();
                        },
                        scoreDirector -> {
                            TspSolution workingSolution = scoreDirector.getWorkingSolution();

                            Location workingLocation = scoreDirector.lookUpWorkingObject(location);
                            if (workingLocation == null) {
                                throw new IllegalStateException("Can't look up working copy of " + location);
                            }
                            // shallow clone fact list
                            // TODO think if we can fail fast when user forgets to make the clone (PLANNER)
                            workingSolution.setLocationList(new ArrayList<>(workingSolution.getLocationList()));
                            scoreDirector.beforeProblemFactRemoved(workingLocation);
                            if (!workingSolution.getLocationList().remove(workingLocation)) {
                                throw new IllegalStateException("This is a bug.");
                            }
                            scoreDirector.afterProblemFactRemoved(workingLocation);

                            scoreDirector.triggerVariableListeners();
                        }
                ));
            }
        }
    }
}
