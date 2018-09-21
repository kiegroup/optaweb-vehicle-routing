package org.optaweb.tsp.optawebtspplanner;

import java.math.BigDecimal;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

@Controller
public class TspMapController {

    private static Logger logger = LoggerFactory.getLogger(TspMapController.class);

    private final PlaceRepository repository;
    private final TspPlannerComponent planner;

    @Autowired
    public TspMapController(PlaceRepository repository, TspPlannerComponent planner) {
        this.repository = repository;
        this.planner = planner;
    }

    @SubscribeMapping("/route")
    public RouteMessage subscribe() {
        logger.info("Subscribed");
        return planner.getSolution();
    }

    @MessageMapping("/place")
    public void create(Place place) {
        Place savedPlace = repository.save(place);
        planner.addPlace(place);
        logger.info("Created {}", savedPlace);
    }

    @MessageMapping("/demo")
    public void demo() {
//        repository.findAll().forEach(planner::removePlace);
//        repository.deleteAll();
        Arrays.stream(Europe.values()).forEach(city -> {
            Place place = new Place(BigDecimal.valueOf(city.lat), BigDecimal.valueOf(city.lng));
            Place savedPlace = repository.save(place);
            planner.addPlace(place);
            logger.info("Created {}", savedPlace);
        });
    }

    @MessageMapping({"/place/{id}/delete"})
    public void delete(@DestinationVariable Long id) {
        repository.findById(id).ifPresent(place -> {
            repository.deleteById(id);
            planner.removePlace(place);
            logger.info("Deleted place {}", id);
        });
    }
}
