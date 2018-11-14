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
        Arrays.stream(Belgium.values()).forEach(city -> {
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
