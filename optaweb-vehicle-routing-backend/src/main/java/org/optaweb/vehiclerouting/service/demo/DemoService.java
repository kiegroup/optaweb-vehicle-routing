/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.optaweb.vehiclerouting.service.demo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.domain.RoutingProblem;
import org.optaweb.vehiclerouting.service.demo.dataset.DataSetMarshaller;
import org.optaweb.vehiclerouting.service.location.LocationRepository;
import org.optaweb.vehiclerouting.service.location.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Performs demo-related use cases.
 */
@Service
public class DemoService {

    static final int MAX_TRIES = 10;

    private final LocationService locationService;
    private final DataSetMarshaller dataSetMarshaller;
    private final LocationRepository locationRepository;
    private final RoutingProblemList routingProblems;

    @Autowired
    DemoService(
            LocationService locationService,
            DataSetMarshaller dataSetMarshaller,
            LocationRepository locationRepository,
            RoutingProblemList routingProblems
    ) {
        this.locationService = locationService;
        this.dataSetMarshaller = dataSetMarshaller;
        this.locationRepository = locationRepository;
        this.routingProblems = routingProblems;
    }

    public Collection<RoutingProblem> demos() {
        return routingProblems.all();
    }

    @Async
    public void loadDemo(String name) {
        RoutingProblem routingProblem = routingProblems.byName(name);
        // Add depot
        routingProblem.depot().ifPresent(depot -> addWithRetry(depot.coordinates(), depot.description()));

        // TODO start randomizing only after using all available cities (=> reproducibility for small demos)
        routingProblem.visits().forEach(visit -> addWithRetry(visit.coordinates(), visit.description()));
    }

    private void addWithRetry(Coordinates coordinates, String description) {
        int tries = 0;
        while (tries < MAX_TRIES && !locationService.createLocation(coordinates, description)) {
            tries++;
        }
        if (tries == MAX_TRIES) {
            throw new RuntimeException(
                    "Impossible to create a new location near " + coordinates + " after " + tries + " attempts"
            );
        }
    }

    public String exportDataSet() {
        // FIXME still relying on the fact that the first location in the repository is the depot
        List<Location> visits = new ArrayList<>(locationRepository.locations());
        Location depot = visits.isEmpty() ? null : visits.remove(0);
        return dataSetMarshaller.marshal(new RoutingProblem("Custom Vehicle Routing instance", depot, visits));
    }
}
