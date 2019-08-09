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

/**
 * Accumulates vehicles, depots and visits until there's enough data to start the optimization.
 * Solutions are published even if solving hasn't started yet due to missing facts (e.g. no vehicles or no visits).
 * Stops solver when vehicles or visits are reduced to zero.
 */
@Component
class RouteOptimizerImpl implements RouteOptimizer {

    private final SolverManager solverManager;
    private final SolutionPublisher solutionPublisher;

    private final List<Vehicle> vehicles = new ArrayList<>();
    private final List<RoadLocation> visits = new ArrayList<>();
    private Depot depot;

    @Autowired
    RouteOptimizerImpl(SolverManager solverManager, SolutionPublisher solutionPublisher) {
        this.solverManager = solverManager;
        this.solutionPublisher = solutionPublisher;
    }

    @Override
    public void addLocation(
            org.optaweb.vehiclerouting.domain.Location domainLocation,
            DistanceMatrix distanceMatrix
    ) {
        RoadLocation location = LocationFactory.fromDomain(domainLocation);
        DistanceMap distanceMap = new DistanceMap(domainLocation, distanceMatrix.getRow(domainLocation));
        location.setTravelDistanceMap(distanceMap);
        // Unfortunately can't start solver with an empty solution (see https://issues.jboss.org/browse/PLANNER-776)
        if (depot == null) {
            depot = DepotFactory.depot(location);
            publishSolution();
        } else {
            visits.add(location);
            if (vehicles.isEmpty()) {
                publishSolution();
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
                throw new IllegalArgumentException("Cannot remove " + domainLocation + " because it doesn't exist");
            }
            depot = null;
            publishSolution();
        } else {
            if (depot.getId().equals(domainLocation.id())) {
                throw new IllegalStateException("You can only remove depot if there are no customers");
            }
            if (!visits.removeIf(item -> item.getId().equals(domainLocation.id()))) {
                throw new IllegalArgumentException("Cannot remove " + domainLocation + " because it doesn't exist");
            }
            if (visits.isEmpty()) {
                solverManager.stopSolver();
                publishSolution();
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
            publishSolution();
        } else if (vehicles.size() == 1) {
            solverManager.startSolver(SolutionFactory.solutionFromLocations(vehicles, depot, visits));
        } else {
            solverManager.addVehicle(vehicle);
        }
    }

    @Override
    public void removeVehicle(org.optaweb.vehiclerouting.domain.Vehicle domainVehicle) {
        if (!vehicles.removeIf(vehicle -> vehicle.getId().equals(domainVehicle.id()))) {
            throw new IllegalArgumentException("Cannot remove " + domainVehicle + " because it doesn't exist");
        }
        if (visits.isEmpty()) {
            publishSolution();
        } else {
            if (vehicles.isEmpty()) {
                solverManager.stopSolver();
                publishSolution();
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
        publishSolution();
    }

    private void publishSolution() {
        solutionPublisher.publishSolution(SolutionFactory.solutionFromLocations(vehicles, depot, visits));
    }
}
