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

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.event.BestSolutionChangedEvent;
import org.optaplanner.core.api.solver.event.SolverEventListener;
import org.optaplanner.examples.vehiclerouting.domain.Depot;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.domain.location.Location;
import org.optaplanner.examples.vehiclerouting.domain.location.RoadLocation;
import org.optaweb.vehiclerouting.domain.Route;
import org.optaweb.vehiclerouting.plugin.planner.change.AddCustomer;
import org.optaweb.vehiclerouting.plugin.planner.change.RemoveCustomer;
import org.optaweb.vehiclerouting.plugin.planner.change.RemoveLocation;
import org.optaweb.vehiclerouting.service.location.DistanceMatrix;
import org.optaweb.vehiclerouting.service.location.RouteOptimizer;
import org.optaweb.vehiclerouting.service.route.RouteChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class RouteOptimizerImpl implements RouteOptimizer,
                                           SolverEventListener<VehicleRoutingSolution> {

    private static final Logger logger = LoggerFactory.getLogger(RouteOptimizerImpl.class);

    private final ApplicationEventPublisher publisher;
    private final Solver<VehicleRoutingSolution> solver;
    private final AsyncTaskExecutor executor;
    private Future<?> solverFuture;
    private VehicleRoutingSolution solution;

    @Autowired
    public RouteOptimizerImpl(ApplicationEventPublisher publisher,
                              Solver<VehicleRoutingSolution> solver,
                              AsyncTaskExecutor executor) {
        this.publisher = publisher;
        this.solver = solver;
        this.executor = executor;

        this.solver.addEventListener(this);
        // TODO make initial solution a dependency?
        solution = SolutionUtil.initialSolution();
    }

    private void publishRoute(VehicleRoutingSolution solution) {
        String distanceString = solution.getDistanceString(new DecimalFormat("#,##0.00"));
        logger.info(
                "New solution with {} depots, {} vehicles, {} customers, distance: {}",
                solution.getDepotList().size(),
                solution.getVehicleList().size(),
                solution.getCustomerList().size(),
                distanceString
        );
        List<Route> routes = SolutionUtil.routes(solution);
        logger.debug("Routes: {}", routes);
        publisher.publishEvent(new RouteChangedEvent(this, distanceString, SolutionUtil.depot(solution), routes));
    }

    private void startSolver() {
        if (solverFuture != null) {
            throw new IllegalStateException("Solver start has already been requested");
        }
        // TODO move this to @Async method?
        // TODO use ListenableFuture to react to solve() exceptions immediately?
        solverFuture = executor.submit(() -> {
            solver.solve(solution);
        });
    }

    boolean isSolving() {
        if (solverFuture == null) {
            return false;
        }
        assertSolverIsAlive();
        return true;
    }

    private void assertSolverIsAlive() {
        if (solverFuture.isDone()) {
            try {
                solverFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException("Solver has died", e);
            }
            throw new IllegalStateException("Solver has finished solving even though it operates in daemon mode.");
        }
    }

    void stopSolver() {
        if (solverFuture != null) {
            // TODO what happens if solver hasn't started yet (solve() is called asynchronously)
            solver.terminateEarly();
            // make sure solver has terminated and propagate exceptions
            try {
                solverFuture.get();
                solverFuture = null;
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException("Failed to stop solver", e);
            }
        }
    }

    @Override
    public void bestSolutionChanged(BestSolutionChangedEvent<VehicleRoutingSolution> bestSolutionChangedEvent) {
        // CAUTION! This runs on the solver thread. Implications:
        // 1. The method should be as quick as possible to avoid blocking solver unnecessarily.
        // 2. This place is a potential source of race conditions.
        if (!bestSolutionChangedEvent.isEveryProblemFactChangeProcessed()) {
            logger.info("Ignoring a new best solution that has some problem facts missing");
            return;
        }
        // TODO do not store best solution, just publish it
        solution = bestSolutionChangedEvent.getNewBestSolution();
        // TODO Race condition, if a servlet thread deletes that location in the middle of this method happening
        //      on the solver thread. Make sure that location is still in the repository.
        //      Maybe repair the solution OR ignore if it's inconsistent (log a WARNING).
        publishRoute(solution);
    }

    @Override
    public void addLocation(org.optaweb.vehiclerouting.domain.Location domainLocation,
                            DistanceMatrix distanceMatrix) {
        RoadLocation location = SolutionUtil.planningLocation(domainLocation);
        DistanceMap distanceMap = new DistanceMap(domainLocation, distanceMatrix.getRow(domainLocation));
        location.setTravelDistanceMap(distanceMap);
        // Unfortunately can't start solver with an empty solution (see https://issues.jboss.org/browse/PLANNER-776)
        if (!isSolving()) {
            switch (solution.getLocationList().size()) {
                case 0:
                    Depot depot = SolutionUtil.addDepot(solution, location);
                    SolutionUtil.moveAllVehiclesTo(solution, depot);
                    publishRoute(solution);
                    break;
                case 1:
                    SolutionUtil.addCustomer(solution, location, SolutionUtil.DEFAULT_CUSTOMER_DEMAND);
                    startSolver();
                    break;
                default:
                    throw new IllegalStateException(
                            "Illegal number of locations when solver is not solving: "
                                    + solution.getLocationList().size()
                    );
            }
        } else {
            solver.addProblemFactChange(new AddCustomer(location));
        }
    }

    @Override
    public void removeLocation(org.optaweb.vehiclerouting.domain.Location domainLocation) {
        Location location = SolutionUtil.planningLocation(domainLocation);
        if (!isSolving()) {
            if (solution.getLocationList().size() != 1) {
                throw new IllegalStateException(
                        "Impossible number of locations (" + solution.getLocationList().size()
                                + ") when solver is not solving.\n"
                                + solution.getLocationList()
                );
            }
            solution.getLocationList().clear();
            solution.getDepotList().clear();
            SolutionUtil.moveAllVehiclesTo(solution, null);
            publishRoute(solution);
        } else {
            if (solution.getDepotList().get(0).getLocation().getId().equals(location.getId())) {
                throw new UnsupportedOperationException("You can only remove depot if there are no customers.");
            }
            if (solution.getCustomerList().size() == 1) {
                // depot and 1 customer remaining
                stopSolver();
                solution.getCustomerList().clear();
                solution.getLocationList().removeIf(l -> l.getId().equals(location.getId()));
                solution.getVehicleList().forEach(vehicle -> vehicle.setNextCustomer(null));
                publishRoute(solution);
            } else {
                solver.addProblemFactChanges(Arrays.asList(new RemoveCustomer(location), new RemoveLocation(location)));
            }
        }
    }

    @Override
    public void clear() {
        stopSolver();
        solution = SolutionUtil.initialSolution();
        publishRoute(solution);
    }
}
