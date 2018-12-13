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

package org.optaweb.tsp.optawebtspplanner.network;

import java.math.BigDecimal;
import java.util.Arrays;

import org.optaweb.tsp.optawebtspplanner.core.LatLng;
import org.optaweb.tsp.optawebtspplanner.core.Location;
import org.optaweb.tsp.optawebtspplanner.demo.Belgium;
import org.optaweb.tsp.optawebtspplanner.persistence.LocationEntity;
import org.optaweb.tsp.optawebtspplanner.persistence.LocationRepository;
import org.optaweb.tsp.optawebtspplanner.planner.RouteChangedEvent;
import org.optaweb.tsp.optawebtspplanner.planner.TspPlannerComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

@Controller
public class TspMapController {

    private static final Logger logger = LoggerFactory.getLogger(TspMapController.class);

    private final LocationRepository repository;
    private final TspPlannerComponent planner;
    private final RoutePublisher routePublisher;

    @Autowired
    public TspMapController(LocationRepository repository,
                            TspPlannerComponent planner,
                            RoutePublisher routePublisher) {
        this.repository = repository;
        this.planner = planner;
        this.routePublisher = routePublisher;
    }

    @SubscribeMapping("/route")
    public RouteMessage subscribe() {
        logger.info("Subscribed");
        RouteChangedEvent event = planner.getSolution();
        return routePublisher.createResponse(event.getDistance(), event.getRoute());
    }

    @MessageMapping("/place")
    public void create(Place place) {
        LocationEntity locationEntity = repository.save(new LocationEntity(place.getLatitude(), place.getLongitude()));
        Location location = new Location(
                locationEntity.getId(),
                new LatLng(locationEntity.getLatitude(), locationEntity.getLongitude())
        );
        planner.addLocation(location);
        logger.info("Created {}", locationEntity);
    }

    @MessageMapping("/demo")
    public void demo() {
        Arrays.stream(Belgium.values()).forEach(city -> {
            LocationEntity locationEntity = repository.save(
                    new LocationEntity(BigDecimal.valueOf(city.lat), BigDecimal.valueOf(city.lng)));
            Location location = new Location(
                    locationEntity.getId(),
                    new LatLng(locationEntity.getLatitude(), locationEntity.getLongitude())
            );
            planner.addLocation(location);
            logger.info("Created {}", location);
        });
    }

    @MessageMapping({"/place/{id}/delete"})
    public void delete(@DestinationVariable Long id) {
        repository.findById(id).ifPresent(locationEntityEntity -> {
            repository.deleteById(id);
            Location location = new Location(
                    id,
                    new LatLng(locationEntityEntity.getLatitude(), locationEntityEntity.getLongitude())
            );
            planner.removeLocation(location);
            logger.info("Deleted {}", location);
        });
    }
}
