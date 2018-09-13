package org.optaweb.tsp.optawebtspplanner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.event.BestSolutionChangedEvent;
import org.optaplanner.core.api.solver.event.SolverEventListener;
import org.optaplanner.examples.tsp.app.TspApp;
import org.optaplanner.examples.tsp.domain.Domicile;
import org.optaplanner.examples.tsp.domain.Standstill;
import org.optaplanner.examples.tsp.domain.TspSolution;
import org.optaplanner.examples.tsp.domain.Visit;
import org.optaplanner.examples.tsp.domain.location.AirLocation;
import org.optaplanner.examples.tsp.domain.location.DistanceType;
import org.optaplanner.examples.tsp.domain.location.Location;
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
    private ThreadPoolTaskExecutor executor;
    private TspSolution tsp = new TspSolution();

    @Autowired
    public TspPlannerComponent(SimpMessagingTemplate webSocket,
                               PlaceRepository repository) {
        this.webSocket = webSocket;
        this.repository = repository;

        tsp.setDistanceType(DistanceType.AIR_DISTANCE);
        tsp.setLocationList(new ArrayList<>());
        tsp.setVisitList(new ArrayList<>());

        SolverFactory<TspSolution> sf = SolverFactory.createFromXmlResource(TspApp.SOLVER_CONFIG);
        sf.getSolverConfig().setDaemon(true);
        solver = sf.buildSolver();
        solver.addEventListener(this);
    }

    private static AirLocation fromPlace(Place p) {
        return new AirLocation(p.getId(), p.getLatitude().doubleValue(), p.getLongitude().doubleValue());
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
        List<Place> route = new ArrayList<>();
        // TODO race condition, if a rest thread deletes that location in the middle of this method happening on the solver thread
        // TODO make sure that location is still in the repository
        // TODO maybe repair the solution OR ignore if it's inconsistent (log WARNING)
        Domicile domicile = tsp.getDomicile();
        addToRoute(repository.findById(domicile.getLocation().getId()), route);
        for (Visit visit = nextVisitMap.get(domicile); visit != null; visit = nextVisitMap.get(visit)) {
            addToRoute(repository.findById(visit.getLocation().getId()), route);
        }
        return Optional.of(route);
    }

    private void sendRoute(List<Place> route) {
        webSocket.convertAndSend("/topic/route", route);
    }

    @Override
    public void bestSolutionChanged(BestSolutionChangedEvent<TspSolution> bestSolutionChangedEvent) {
        if (!bestSolutionChangedEvent.isEveryProblemFactChangeProcessed()) {
            logger.info("Ignoring a new best solution that has some problem facts missing");
            return;
        }
        tsp = bestSolutionChangedEvent.getNewBestSolution();
        extractRoute(tsp).ifPresent(route -> {
            logger.info("New TSP with {} locations, {} route: {}",
                        tsp.getLocationList().size(),
                        tsp.getVisitList().size(),
                        route);
            sendRoute(route);
        });
    }

    public void addPlace(Place place) {
        AirLocation airLocation = fromPlace(place);
        // Unfortunately can't start solver with an empty solution (see https://issues.jboss.org/browse/PLANNER-776)
        if (!solver.isSolving()) {
            List<Location> locationList = tsp.getLocationList();
            locationList.add(airLocation);
            if (locationList.size() == 1) {
                Domicile domicile = new Domicile();
                domicile.setId(airLocation.getId());
                domicile.setLocation(airLocation);
                tsp.setDomicile(domicile);
            } else if (locationList.size() == 2) {
                Visit visit = new Visit();
                visit.setId(airLocation.getId());
                visit.setLocation(airLocation);
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
            sendRoute(extractRoute(tsp).orElseThrow(
                    () -> new IllegalStateException("No unconnected visits expected at this point.")));
        } else {
            solver.addProblemFactChange(scoreDirector -> {
                TspSolution workingSolution = scoreDirector.getWorkingSolution();
                workingSolution.setLocationList(new ArrayList<>(workingSolution.getLocationList()));

                scoreDirector.beforeProblemFactAdded(airLocation);
                workingSolution.getLocationList().add(airLocation);
                scoreDirector.afterProblemFactAdded(airLocation);

                Visit visit = new Visit();
                visit.setId(place.getId());
                visit.setLocation(airLocation);

                scoreDirector.beforeEntityAdded(visit);
                workingSolution.getVisitList().add(visit);
                scoreDirector.afterEntityAdded(visit);

                scoreDirector.triggerVariableListeners();
            });
        }
    }

    public void removePlace(Place place) {
        AirLocation airLocation = fromPlace(place);
        if (!solver.isSolving()) {
            tsp.getLocationList().remove(0);
            tsp.setDomicile(null);
        } else {
            if (tsp.getVisitList().size() == 1) {
                // domicile and 1 visit remaining
                solver.terminateEarly();
                executor.shutdown();
                tsp.getVisitList().remove(0);
                tsp.getLocationList().removeIf(location -> location.getId().equals(airLocation.getId()));
                Location lastLocation = tsp.getLocationList().get(0);
                Domicile domicile = new Domicile();
                domicile.setId(lastLocation.getId());
                domicile.setLocation(lastLocation);
                tsp.setDomicile(domicile);
            } else {
                if (tsp.getDomicile().getLocation().getId().equals(airLocation.getId())) {
                    throw new UnsupportedOperationException("You can only remove domicile if it's the only location on map.");
                }
                solver.addProblemFactChanges(Arrays.asList(
                        scoreDirector -> {
                            TspSolution workingSolution = scoreDirector.getWorkingSolution();
                            Visit visit = workingSolution.getVisitList().stream()
                                    .filter(v -> v.getLocation().getId().equals(airLocation.getId()))
                                    .findFirst()
                                    .orElseThrow(() -> new IllegalArgumentException(
                                            "Invalid request for removing visit at " + airLocation));

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

                            AirLocation workingAirLocation = scoreDirector.lookUpWorkingObject(airLocation);
                            if (workingAirLocation == null) {
                                throw new IllegalStateException("Can't look up working copy of " + airLocation);
                            }
                            // shallow clone fact list
                            // TODO think if we can fail fast when user forgets to make the clone (PLANNER)
                            workingSolution.setLocationList(new ArrayList<>(workingSolution.getLocationList()));
                            scoreDirector.beforeProblemFactRemoved(workingAirLocation);
                            if (!workingSolution.getLocationList().remove(workingAirLocation)) {
                                throw new IllegalStateException("This is a bug.");
                            }
                            scoreDirector.afterProblemFactRemoved(workingAirLocation);

                            scoreDirector.triggerVariableListeners();
                        }
                ));
            }
        }
    }
}
