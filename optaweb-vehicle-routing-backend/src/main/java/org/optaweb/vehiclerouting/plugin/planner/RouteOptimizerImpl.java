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

import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.domain.Vehicle;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningDepot;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningLocation;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningLocationFactory;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVehicle;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVehicleFactory;
import org.optaweb.vehiclerouting.plugin.planner.domain.SolutionFactory;
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

    private final List<PlanningVehicle> vehicles = new ArrayList<>();
    private final List<PlanningLocation> visits = new ArrayList<>();
    private PlanningDepot depot;

    @Autowired
    RouteOptimizerImpl(SolverManager solverManager, SolutionPublisher solutionPublisher) {
        this.solverManager = solverManager;
        this.solutionPublisher = solutionPublisher;
    }

    @Override
    public void addLocation(Location domainLocation, DistanceMatrix distanceMatrix) {
        PlanningLocation location = PlanningLocationFactory.fromDomain(domainLocation);
        DistanceMap distanceMap = new DistanceMap(location, distanceMatrix.getRow(domainLocation));
        location.setTravelDistanceMap(distanceMap);
        // Unfortunately can't start solver with an empty solution (see https://issues.redhat.com/browse/PLANNER-776)
        if (depot == null) {
            depot = new PlanningDepot(location);
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
    public void removeLocation(Location domainLocation) {
        if (visits.isEmpty()) {
            if (depot == null) {
                throw new IllegalArgumentException(
                        "Cannot remove " + domainLocation + " because there are no locations"
                );
            }
            if (depot.getId() != domainLocation.id()) {
                throw new IllegalArgumentException("Cannot remove " + domainLocation + " because it doesn't exist");
            }
            depot = null;
            publishSolution();
        } else {
            if (depot.getId() == domainLocation.id()) {
                throw new IllegalStateException("You can only remove depot if there are no visits");
            }
            if (!visits.removeIf(item -> item.getId() == domainLocation.id())) {
                throw new IllegalArgumentException("Cannot remove " + domainLocation + " because it doesn't exist");
            }
            if (vehicles.isEmpty()) { // solver is not running
                publishSolution();
            } else if (visits.isEmpty()) { // solver is running
                solverManager.stopSolver();
                publishSolution();
            } else {
                solverManager.removeLocation(PlanningLocationFactory.fromDomain(domainLocation));
            }
        }
    }

    @Override
    public void addVehicle(Vehicle domainVehicle) {
        PlanningVehicle vehicle = PlanningVehicleFactory.fromDomain(domainVehicle);
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
    public void removeVehicle(Vehicle domainVehicle) {
        if (!vehicles.removeIf(vehicle -> vehicle.getId() == domainVehicle.id())) {
            throw new IllegalArgumentException("Cannot remove " + domainVehicle + " because it doesn't exist");
        }
        if (visits.isEmpty()) { // solver is not running
            publishSolution();
        } else if (vehicles.isEmpty()) { // solver is running
            solverManager.stopSolver();
            publishSolution();
        } else {
            solverManager.removeVehicle(PlanningVehicleFactory.fromDomain(domainVehicle));
        }
    }

    @Override
    public void changeCapacity(Vehicle domainVehicle) {
        PlanningVehicle vehicle = vehicles.stream()
                .filter(item -> item.getId() == domainVehicle.id())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Cannot change capacity of " + domainVehicle + " because it doesn't exist"
                ));
        vehicle.setCapacity(domainVehicle.capacity());
        if (!visits.isEmpty()) {
            solverManager.changeCapacity(vehicle);
        } else {
            publishSolution();
        }
    }

    @Override
    public void removeAllLocations() {
        solverManager.stopSolver();
        depot = null;
        visits.clear();
        publishSolution();
    }

    @Override
    public void removeAllVehicles() {
        solverManager.stopSolver();
        vehicles.clear();
        publishSolution();
    }

    private void publishSolution() {
        solutionPublisher.publishSolution(SolutionFactory.solutionFromLocations(vehicles, depot, visits));
    }
}
