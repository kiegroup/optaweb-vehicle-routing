package org.optaweb.tsp.optawebtspplanner;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TspPlannerComponent {

    private static Logger logger = LoggerFactory.getLogger(TspMapController.class);

    private final SimpMessagingTemplate webSocket;
    private final PlaceRepository repository;
    private final Solver<TspSolution> solver;
    private TspSolution tsp = new TspSolution();

    @Autowired
    public TspPlannerComponent(SimpMessagingTemplate webSocket,
                               PlaceRepository repository) {
        this.webSocket = webSocket;
        this.repository = repository;
        SolverFactory<TspSolution> sf = SolverFactory.createFromXmlResource(TspApp.SOLVER_CONFIG);
        sf.getSolverConfig().setDaemon(true);
        solver = sf.buildSolver();
        tsp.setDistanceType(DistanceType.AIR_DISTANCE);
        tsp.setLocationList(new ArrayList<>());
        tsp.setVisitList(new ArrayList<>());
    }

    public void addPlace(Place place) {
        AirLocation airLocation = fromPlace(place);
        List<Location> locationList = tsp.getLocationList();
        // Unfortunately can't start solver with an empty solution (see https://issues.jboss.org/browse/PLANNER-776)
        if (locationList.size() < 2) {
            locationList.add(airLocation);
            if (locationList.size() == 1) {
                Domicile domicile = new Domicile();
                domicile.setId(airLocation.getId());
                domicile.setLocation(airLocation);
                tsp.setDomicile(domicile);
            } else {
                Visit visit = new Visit();
                visit.setId(airLocation.getId());
                visit.setLocation(airLocation);
                tsp.getVisitList().add(visit);
                solver.solve(tsp);
            }
        } else {
            solver.addProblemFactChange(scoreDirector -> {
                scoreDirector.beforeProblemFactAdded(airLocation);
                scoreDirector.getWorkingSolution().getLocationList().add(airLocation);
                scoreDirector.afterProblemFactAdded(airLocation);
                scoreDirector.triggerVariableListeners();
            });
            solver.addProblemFactChange(scoreDirector -> {
                Visit visit = new Visit();
                visit.setId(place.getId());
                visit.setLocation(airLocation);

                scoreDirector.beforeEntityAdded(visit);
                scoreDirector.getWorkingSolution().getVisitList().add(visit);
                scoreDirector.afterEntityAdded(visit);
                scoreDirector.triggerVariableListeners();
            });
        }
    }

    private static AirLocation fromPlace(Place p) {
        return new AirLocation(p.getId(), p.getLatitude().doubleValue(), p.getLongitude().doubleValue());
    }

    @Scheduled(fixedRate = 1000)
    public void sendMessage() {
        if (!solver.isSolving()) {
            return;
        }
        TspSolution tspSolution = solver.getBestSolution();
        Domicile domicile = tspSolution.getDomicile();
        Map<Standstill, Visit> nextVisitMap = new LinkedHashMap<>();
        List<Visit> unassignedVisitList = new ArrayList<>();
        for (Visit visit : tspSolution.getVisitList()) {
            if (visit.getPreviousStandstill() != null) {
                nextVisitMap.put(visit.getPreviousStandstill(), visit);
            } else {
                unassignedVisitList.add(visit);
            }
        }
        List<Place> route = new ArrayList<>();
        route.add(repository.findById(domicile.getLocation().getId()).get());
        for (Visit visit = nextVisitMap.get(domicile); visit != null; visit = nextVisitMap.get(visit)) {
            route.add(repository.findById(visit.getLocation().getId()).get());
        }

        logger.info("Route: {}", route);
        webSocket.convertAndSend("/topic/route", route);
    }
}
