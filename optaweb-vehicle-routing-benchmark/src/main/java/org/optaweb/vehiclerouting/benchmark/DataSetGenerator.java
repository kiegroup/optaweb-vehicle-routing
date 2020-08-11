/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaweb.vehiclerouting.benchmark;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.domain.LocationData;
import org.optaweb.vehiclerouting.domain.RoutingProblem;
import org.optaweb.vehiclerouting.service.distance.DistanceCalculationException;
import org.optaweb.vehiclerouting.service.distance.DistanceCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DataSetGenerator {

    private static final Logger logger = LoggerFactory.getLogger(DataSetGenerator.class);
    private static final int MAX_TRIES = 10;
    private final DistanceCalculator distanceCalculator;
    private final RoutingProblem problem;
    private final int maxDescriptionLength;

    DataSetGenerator(DistanceCalculator distanceCalculator, RoutingProblem problem) {
        this.distanceCalculator = distanceCalculator;
        this.problem = problem;
        maxDescriptionLength = problem.visits().stream().mapToInt(v -> v.description().length()).max().orElse(0);
    }

    static LocationData nextDatSetItem(RoutingProblem problem, int i) {
        return problem.visits().get(i % problem.visits().size());
    }

    static Coordinates randomize(Coordinates coords) {
        return Coordinates.valueOf(
                // Randomize the original coordinates in the interval of ± 0.04° (± 4,4 km).
                coords.latitude().doubleValue() + Math.random() * 0.08 - 0.04,
                coords.longitude().doubleValue() + Math.random() * 0.08 - 0.04);
    }

    static Location randomizedLocation(long id, LocationData locationData) {
        return new Location(id, randomize(locationData.coordinates()), locationData.description());
    }

    List<Location> generate(int locationCount) {
        AtomicLong idSequence = new AtomicLong();
        ArrayList<Location> locations = new ArrayList<>(locationCount);

        for (int i = 0; i < locationCount; i++) {
            LocationData locationData = nextDatSetItem(problem, i);
            long id = idSequence.incrementAndGet();
            Location to = randomizedLocation(id, locationData);

            if (!locations.isEmpty()) {
                Location from = locations.get(locations.size() - 1);
                int tries = 0;
                while (tries < MAX_TRIES && !isReachable(from, to)) {
                    tries++;
                    logger.warn("Randomized location {} is unreachable.", to.fullDescription());
                    to = randomizedLocation(id, locationData);
                }
                if (tries == MAX_TRIES) {
                    throw new RuntimeException("Impossible to create a new location near " + locationData
                            + " after " + tries + " attempts");
                }
            }
            String leftPadding = IntStream.range(Long.toString(to.id()).length(), Integer.toString(locationCount).length())
                    .mapToObj(operand -> " ")
                    .collect(joining());
            String rightPadding = IntStream.range(to.description().length(), maxDescriptionLength)
                    .mapToObj(operand -> " ")
                    .collect(joining());
            logger.info("Generated randomized location {}{}{} {}.", leftPadding, to, rightPadding, to.coordinates());
            locations.add(to);
        }
        return locations;
    }

    private boolean isReachable(Location from, Location to) {
        try {
            distanceCalculator.travelTimeMillis(from.coordinates(), to.coordinates());
            return true;
        } catch (DistanceCalculationException e) {
            return false;
        }
    }
}
