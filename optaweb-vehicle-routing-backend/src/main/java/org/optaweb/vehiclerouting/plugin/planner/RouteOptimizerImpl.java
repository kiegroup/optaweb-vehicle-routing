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

package org.optaweb.vehiclerouting.plugin.planner;

import java.util.ArrayList;
import java.util.List;

import org.optaplanner.examples.vehiclerouting.domain.Depot;
import org.optaplanner.examples.vehiclerouting.domain.Vehicle;
import org.optaplanner.examples.vehiclerouting.domain.location.RoadLocation;
import org.optaweb.vehiclerouting.service.location.DistanceMatrix;
import org.optaweb.vehiclerouting.service.location.RouteOptimizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class RouteOptimizerImpl implements RouteOptimizer {

    private final SolverManager solverManager;
    private final RouteChangedEventPublisher eventPublisher;

    private final List<Vehicle> vehicles = new ArrayList<>();
    private final List<RoadLocation> visits = new ArrayList<>();
    private Depot depot;

    @Autowired
    RouteOptimizerImpl(SolverManager solverManager, RouteChangedEventPublisher eventPublisher) {
        this.solverManager = solverManager;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void addLocation(org.optaweb.vehiclerouting.domain.Location domainLocation,
                            DistanceMatrix distanceMatrix) {
        RoadLocation location = LocationFactory.fromDomain(domainLocation);
        DistanceMap distanceMap = new DistanceMap(domainLocation, distanceMatrix.getRow(domainLocation));
        location.setTravelDistanceMap(distanceMap);
        // Unfortunately can't start solver with an empty solution (see https://issues.jboss.org/browse/PLANNER-776)
        if (depot == null) {
            depot = DepotFactory.depot(location);
            publishRoute();
        } else {
            visits.add(location);
            if (vehicles.isEmpty()) {
                publishRoute();
            } else if (visits.size() == 1) {
                solverManager.startSolver(SolutionFactory.solutionFromLocations(vehicles, depot, visits));
            } else {
                solverManager.addLocation(location);
            }
        }
    }

    @Override
    public void removeLocation(org.optaweb.vehiclerouting.domain.Location domainLocation) {
        if (visits.isEmpty()) {
            if (depot == null) {
                throw new IllegalArgumentException(
                        "Cannot remove " + domainLocation + " because there are no locations"
                );
            }
            if (!depot.getId().equals(domainLocation.id())) {
                throw new IllegalArgumentException(
                        "Cannot remove " + domainLocation + " because it doesn't exist"
                );
            }
            depot = null;
            publishRoute();
        } else {
            if (depot.getId().equals(domainLocation.id())) {
                throw new IllegalStateException("You can only remove depot if there are no customers.");
            }
            if (!visits.removeIf(item -> item.getId().equals(domainLocation.id()))) {
                throw new IllegalArgumentException(
                        "Cannot remove " + domainLocation + " because it doesn't exist"
                );
            }
            if (visits.isEmpty()) {
                solverManager.stopSolver();
                publishRoute();
            } else {
                solverManager.removeLocation(LocationFactory.fromDomain(domainLocation));
            }
        }
    }

    @Override
    public void addVehicle(org.optaweb.vehiclerouting.domain.Vehicle domainVehicle) {
        Vehicle vehicle = VehicleFactory.fromDomain(domainVehicle);
        vehicle.setDepot(depot);
        vehicles.add(vehicle);
        if (visits.isEmpty()) {
            publishRoute();
        } else if (vehicles.size() == 1) {
            solverManager.startSolver(SolutionFactory.solutionFromLocations(vehicles, depot, visits));
        } else {
            solverManager.addVehicle(vehicle);
        }
    }

    @Override
    public void removeVehicle(org.optaweb.vehiclerouting.domain.Vehicle domainVehicle) {
        if (!vehicles.removeIf(vehicle -> vehicle.getId().equals(domainVehicle.id()))) {
            throw new IllegalArgumentException("Attempt to remove a non-existent vehicle: " + domainVehicle);
        }
        if (visits.isEmpty()) {
            publishRoute();
        } else {
            if (vehicles.isEmpty()) {
                solverManager.stopSolver();
                publishRoute();
            } else {
                solverManager.removeVehicle(VehicleFactory.fromDomain(domainVehicle));
            }
        }
    }

    @Override
    public void clear() {
        solverManager.stopSolver();
        depot = null;
        // TODO keep vehicles, only remove depot and visits
        vehicles.clear();
        visits.clear();
        publishRoute();
    }

    private void publishRoute() {
        eventPublisher.publishRoute(SolutionFactory.solutionFromLocations(vehicles, depot, visits));
    }
}
