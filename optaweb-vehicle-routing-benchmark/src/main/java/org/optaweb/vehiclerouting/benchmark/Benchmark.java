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

import java.util.concurrent.atomic.AtomicLong;

import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.domain.LocationData;
import org.optaweb.vehiclerouting.domain.RoutingProblem;
import org.optaweb.vehiclerouting.plugin.routing.GraphHopperRouter;
import org.optaweb.vehiclerouting.plugin.routing.RoutingConfig;
import org.optaweb.vehiclerouting.plugin.routing.RoutingProperties;
import org.optaweb.vehiclerouting.service.demo.RoutingProblemConfig;
import org.optaweb.vehiclerouting.service.demo.dataset.DataSetMarshaller;
import org.optaweb.vehiclerouting.service.distance.DistanceCalculationException;
import org.optaweb.vehiclerouting.service.distance.DistanceMatrixImpl;
import org.optaweb.vehiclerouting.service.location.DistanceMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Benchmark {

    private static final Logger logger = LoggerFactory.getLogger(Benchmark.class);
    private static final int MAX_TRIES = 3;
    private final DistanceMatrix distanceMatrix;
    private final RoutingProblem dataset;
    private final int locationCount;

    static Coordinates randomize(Coordinates coords) {
        return Coordinates.valueOf(
                coords.latitude().doubleValue() + Math.random() * 0.08 - 0.04,
                coords.longitude().doubleValue() + Math.random() * 0.08 - 0.04);
    }

    static Location randomizedLocation(long id, LocationData locationData) {
        return new Location(id, randomize(locationData.coordinates()), locationData.description());
    }

    public static void main(String[] args) {
        int locationCount = args.length > 0 ? Integer.parseInt(args[0]) : 50;

        RoutingProperties routingProperties = new RoutingProperties();
        routingProperties.setGhDir("local/graphhopper");
        routingProperties.setOsmDir("local/openstreetmap");
        routingProperties.setOsmFile("belgium-latest.osm.pbf");

        RoutingConfig routingConfig = new RoutingConfig(routingProperties);
        GraphHopperRouter router = new GraphHopperRouter(routingConfig.graphHopper());

        DistanceMatrixImpl distanceMatrix = new DistanceMatrixImpl(router, new NoopDistanceRepository());
        RoutingProblem dataset = new DataSetMarshaller().unmarshal(RoutingProblemConfig.belgiumReader());
        new Benchmark(distanceMatrix, dataset, locationCount).run();
    }

    public Benchmark(DistanceMatrix distanceMatrix, RoutingProblem dataset, int locationCount) {
        this.distanceMatrix = distanceMatrix;
        this.dataset = dataset;
        this.locationCount = locationCount;
    }

    LocationData nextDatSetItem(int i) {
        return dataset.visits().get(i % dataset.visits().size());
    }

    boolean addToMatrix(Location location) {
        try {
            distanceMatrix.addLocation(location);
            logger.info("Added {}", location);
            return true;
        } catch (DistanceCalculationException ex) {
            return false;
        }
    }

    private void run() {
        AtomicLong idSequence = new AtomicLong();

        StopWatch stopWatch = StopWatch.start();

        for (int i = 0; i < locationCount; i++) {
            LocationData locationData = nextDatSetItem(i);
            long id = idSequence.incrementAndGet();
            int tries = 0;
            while (tries < MAX_TRIES && !addToMatrix(randomizedLocation(id, locationData))) {
                tries++;
            }
            if (tries == MAX_TRIES) {
                throw new RuntimeException("Impossible to create a new location near " + locationData
                        + " after " + tries + " attempts");
            }
            stopWatch.lap();
        }
        stopWatch.print();
    }
}
