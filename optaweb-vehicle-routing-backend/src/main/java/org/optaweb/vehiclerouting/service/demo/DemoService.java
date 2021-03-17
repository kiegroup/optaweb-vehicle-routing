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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.domain.RoutingProblem;
import org.optaweb.vehiclerouting.domain.Vehicle;
import org.optaweb.vehiclerouting.service.demo.dataset.DataSetMarshaller;
import org.optaweb.vehiclerouting.service.location.LocationRepository;
import org.optaweb.vehiclerouting.service.location.LocationService;
import org.optaweb.vehiclerouting.service.vehicle.VehicleRepository;
import org.optaweb.vehiclerouting.service.vehicle.VehicleService;

/**
 * Performs demo-related use cases.
 */
@ApplicationScoped
public class DemoService {

    static final int MAX_TRIES = 10;

    private final RoutingProblemList routingProblems;
    private final LocationService locationService;
    private final LocationRepository locationRepository;
    private final VehicleService vehicleService;
    private final VehicleRepository vehicleRepository;
    private final DataSetMarshaller dataSetMarshaller;

    @Inject
    public DemoService(
            RoutingProblemList routingProblems,
            LocationService locationService,
            LocationRepository locationRepository,
            VehicleService vehicleService,
            VehicleRepository vehicleRepository,
            DataSetMarshaller dataSetMarshaller) {
        this.routingProblems = routingProblems;
        this.locationService = locationService;
        this.locationRepository = locationRepository;
        this.vehicleService = vehicleService;
        this.vehicleRepository = vehicleRepository;
        this.dataSetMarshaller = dataSetMarshaller;
    }

    public Collection<RoutingProblem> demos() {
        return routingProblems.all();
    }

    public void loadDemo(String name) {
        RoutingProblem routingProblem = routingProblems.byName(name);
        // Add depot
        routingProblem.depot().ifPresent(depot -> addWithRetry(depot.coordinates(), depot.description()));

        // TODO start randomizing only after using all available cities (=> reproducibility for small demos)
        routingProblem.visits().forEach(visit -> addWithRetry(visit.coordinates(), visit.description()));
        routingProblem.vehicles().forEach(vehicleService::createVehicle);
    }

    private void addWithRetry(Coordinates coordinates, String description) {
        int tries = 0;
        while (tries < MAX_TRIES && !locationService.createLocation(coordinates, description).isPresent()) {
            tries++;
        }
        if (tries == MAX_TRIES) {
            throw new RuntimeException(
                    "Impossible to create a new location near " + coordinates + " after " + tries + " attempts");
        }
    }

    public String exportDataSet() {
        // FIXME still relying on the fact that the first location in the repository is the depot
        List<Location> visits = new ArrayList<>(locationRepository.locations());
        Location depot = visits.isEmpty() ? null : visits.remove(0);
        List<Vehicle> vehicles = vehicleRepository.vehicles();
        return dataSetMarshaller.marshal(new RoutingProblem(
                "Custom Vehicle Routing instance", vehicles, depot, visits));
    }
}
