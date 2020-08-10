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

import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.plugin.routing.GraphHopperRouter;
import org.optaweb.vehiclerouting.plugin.routing.RoutingConfig;
import org.optaweb.vehiclerouting.plugin.routing.RoutingProperties;
import org.optaweb.vehiclerouting.service.distance.DistanceMatrixImpl;
import org.optaweb.vehiclerouting.service.location.DistanceMatrix;
import org.optaweb.vehiclerouting.service.location.DistanceMatrixRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Benchmark {

    private static final Logger logger = LoggerFactory.getLogger(Benchmark.class);
    private final DistanceMatrix distanceMatrix;

    public static void main(String[] args) {
        RoutingProperties routingProperties = new RoutingProperties();
        routingProperties.setGhDir("local/graphhopper");
        routingProperties.setOsmDir("local/openstreetmap");
        routingProperties.setOsmFile("belgium-latest.osm.pbf");

        RoutingConfig routingConfig = new RoutingConfig(routingProperties);
        GraphHopperRouter router = new GraphHopperRouter(routingConfig.graphHopper());

        DistanceMatrixImpl distanceMatrix = new DistanceMatrixImpl(router, new NoopDistanceRepository());

        new Benchmark(distanceMatrix).run();
    }

    public Benchmark(DistanceMatrix distanceMatrix) {
        this.distanceMatrix = distanceMatrix;
    }

    private void run() {
        DistanceMatrixRow row1 = distanceMatrix.addLocation(new Location(1, Coordinates.valueOf(50.583333, 5.5), "Seraing"));
        DistanceMatrixRow row2 = distanceMatrix.addLocation(new Location(2, Coordinates.valueOf(50.966667, 5.5), "Genk"));

        logger.info("1->2: {}", row1.distanceTo(2));
        logger.info("2->1: {}", row2.distanceTo(1));
    }
}
