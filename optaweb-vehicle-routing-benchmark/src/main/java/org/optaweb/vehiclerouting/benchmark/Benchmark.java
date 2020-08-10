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

import java.util.List;

import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.domain.RoutingProblem;
import org.optaweb.vehiclerouting.plugin.routing.GraphHopperRouter;
import org.optaweb.vehiclerouting.plugin.routing.RoutingConfig;
import org.optaweb.vehiclerouting.plugin.routing.RoutingProperties;
import org.optaweb.vehiclerouting.service.demo.RoutingProblemConfig;
import org.optaweb.vehiclerouting.service.demo.dataset.DataSetMarshaller;
import org.optaweb.vehiclerouting.service.distance.DistanceMatrixImpl;
import org.optaweb.vehiclerouting.service.location.DistanceMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Benchmark {

    private static final Logger logger = LoggerFactory.getLogger(Benchmark.class);
    private final DistanceMatrix distanceMatrix;
    private final List<Location> dataset;

    public static void main(String[] args) {
        int locationCount = args.length > 0 ? Integer.parseInt(args[0]) : 50;

        RoutingProperties routingProperties = new RoutingProperties();
        routingProperties.setGhDir("local/graphhopper");
        routingProperties.setOsmDir("local/openstreetmap");
        routingProperties.setOsmFile("belgium-latest.osm.pbf");

        RoutingConfig routingConfig = new RoutingConfig(routingProperties);
        GraphHopperRouter router = new GraphHopperRouter(routingConfig.graphHopper());

        DistanceMatrixImpl distanceMatrix = new DistanceMatrixImpl(router, new NoopDistanceRepository());
        RoutingProblem problem = new DataSetMarshaller().unmarshal(RoutingProblemConfig.belgiumReader());
        DataSetGenerator dataSetGenerator = new DataSetGenerator(router, problem);
        List<Location> dataset = dataSetGenerator.generate(locationCount);

        new Benchmark(distanceMatrix, dataset).run();
    }

    public Benchmark(DistanceMatrix distanceMatrix, List<Location> dataset) {
        this.distanceMatrix = distanceMatrix;
        this.dataset = dataset;
    }

    private void run() {
        StopWatch stopWatch = StopWatch.start();

        dataset.forEach(location -> {
            distanceMatrix.addLocation(location);
            logger.info("Added {}", location);
            stopWatch.lap();
        });

        stopWatch.print();
    }
}
