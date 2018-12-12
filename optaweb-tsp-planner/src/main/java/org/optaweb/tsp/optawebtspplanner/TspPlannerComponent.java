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
import org.optaplanner.examples.tsp.domain.location.RoadLocation;
import org.optaweb.tsp.optawebtspplanner.core.LatLng;
import org.optaweb.tsp.optawebtspplanner.network.Place;
import org.optaweb.tsp.optawebtspplanner.network.RouteMessage;
import org.optaweb.tsp.optawebtspplanner.persistence.Location;
import org.optaweb.tsp.optawebtspplanner.persistence.LocationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class TspPlannerComponent implements SolverEventListener<TspSolution> {

    private static final Logger logger = LoggerFactory.getLogger(TspPlannerComponent.class);

    private final SimpMessagingTemplate webSocket;
    private final LocationRepository repository;
    private final Solver<TspSolution> solver;
    private final ScoreDirector<TspSolution> scoreDirector;
    private ThreadPoolTaskExecutor executor;
    private TspSolution tsp = new TspSolution();
    private List<org.optaplanner.examples.tsp.domain.location.Location> locations = new ArrayList<>();
    private RoutingComponent routing;

    @Autowired
    public TspPlannerComponent(SimpMessagingTemplate webSocket,
                               LocationRepository repository,
                               RoutingComponent routing) {
        this.webSocket = webSocket;
        this.repository = repository;
        this.routing = routing;

        tsp.setLocationList(new ArrayList<>());
        tsp.setVisitList(new ArrayList<>());

        SolverFactory<TspSolution> sf = SolverFactory.createFromXmlResource(TspApp.SOLVER_CONFIG);
        sf.getSolverConfig().setDaemon(true);
        solver = sf.buildSolver();
        scoreDirector = solver.getScoreDirectorFactory().buildScoreDirector();
        solver.addEventListener(this);
    }

    private static RoadLocation fromPlace(Place p) {
        return new RoadLocation(p.getId(), p.getLatitude().doubleValue(), p.getLongitude().doubleValue());
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
        route.add(getPlaceFromRepositoryById(domicile.getLocation().getId()));
        for (Visit visit = nextVisitMap.get(domicile); visit != null; visit = nextVisitMap.get(visit)) {
            route.add(getPlaceFromRepositoryById(visit.getLocation().getId()));
        }
        return Optional.of(route);
    }

    private Place getPlaceFromRepositoryById(long id) {
        Optional<Location> maybeLocation = repository.findById(id);
        if (!maybeLocation.isPresent()) {
            logger.info("Invalid solution, visit #{} has been removed previously", id);
            throw new IllegalStateException("Visit #" + id + " has been removed previously.");
        }
        Location location = maybeLocation.get();
        return new Place(location.getId(), location.getLatitude(), location.getLongitude());
    }

    private RouteMessage createResponse(TspSolution solution, List<Place> route) {
        List<List<Place>> segments = new ArrayList<>();
        for (int i = 1; i < route.size() + 1; i++) {
            // "trick" to get N -> 0 distance at the end of the loop
            Place fromPlace = route.get(i - 1);
            Place toPlace = route.get(i % route.size());
            LatLng fromLatLng = new LatLng(fromPlace.getLatitude(), fromPlace.getLongitude());
            LatLng toLatLng = new LatLng(toPlace.getLatitude(), toPlace.getLongitude());
            List<LatLng> latLngs = routing.getRoute(fromLatLng, toLatLng);
            segments.add(latLngs.stream()
                    .map(latLng -> new Place(0, latLng.getLatitude(), latLng.getLongitude()))
                    .collect(Collectors.toList())
            );
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
        for (org.optaplanner.examples.tsp.domain.location.Location other : locations) {
            RoadLocation toLocation = (RoadLocation) other;
            // TODO handle no route -> roll back the problem fact change
            LatLng fromLatLng = new LatLng(place.getLatitude(), place.getLongitude());
            LatLng toLatLng = new LatLng(BigDecimal.valueOf(toLocation.getLatitude()), BigDecimal.valueOf(toLocation.getLongitude()));
            distanceMap
                    .put(toLocation, routing.getDistance(fromLatLng, toLatLng));
            toLocation.getTravelDistanceMap()
                    .put(location, routing.getDistance(toLatLng, fromLatLng));
        }
        locations.add(location);
        // Unfortunately can't start solver with an empty solution (see https://issues.jboss.org/browse/PLANNER-776)
        if (!solver.isSolving()) {
            List<org.optaplanner.examples.tsp.domain.location.Location> locationList = tsp.getLocationList();
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
        org.optaplanner.examples.tsp.domain.location.Location location = fromPlace(place);
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
                org.optaplanner.examples.tsp.domain.location.Location lastLocation = tsp.getLocationList().get(0);
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

                            org.optaplanner.examples.tsp.domain.location.Location workingLocation = scoreDirector.lookUpWorkingObject(location);
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
