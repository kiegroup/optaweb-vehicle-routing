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

package org.optaweb.vehiclerouting.plugin.planner;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.event.BestSolutionChangedEvent;
import org.optaplanner.core.api.solver.event.SolverEventListener;
import org.optaplanner.examples.vehiclerouting.domain.Vehicle;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.domain.location.Location;
import org.optaweb.vehiclerouting.plugin.planner.change.AddCustomer;
import org.optaweb.vehiclerouting.plugin.planner.change.AddVehicle;
import org.optaweb.vehiclerouting.plugin.planner.change.RemoveCustomer;
import org.optaweb.vehiclerouting.plugin.planner.change.RemoveLocation;
import org.optaweb.vehiclerouting.plugin.planner.change.RemoveVehicle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Component;

@Component
class SolverManager implements SolverEventListener<VehicleRoutingSolution> {

    private static final Logger logger = LoggerFactory.getLogger(SolverManager.class);

    private final Solver<VehicleRoutingSolution> solver;
    private final AsyncTaskExecutor executor;
    private final SolutionPublisher solutionPublisher;

    private Future<VehicleRoutingSolution> solverFuture;

    SolverManager(
            Solver<VehicleRoutingSolution> solver,
            AsyncTaskExecutor executor,
            SolutionPublisher solutionPublisher
    ) {
        this.solver = solver;
        this.executor = executor;
        this.solutionPublisher = solutionPublisher;
        this.solver.addEventListener(this);
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
        // TODO Race condition, if a servlet thread deletes that location in the middle of this method happening
        //      on the solver thread. Make sure that location is still in the repository.
        //      Maybe repair the solution OR ignore if it's inconsistent (log a WARNING).
        solutionPublisher.publishSolution(bestSolutionChangedEvent.getNewBestSolution()); // TODO @Async
    }

    void startSolver(VehicleRoutingSolution solution) {
        if (solverFuture != null) {
            throw new IllegalStateException("Solver start has already been requested");
        }
        // TODO move this to @Async method?
        // TODO use ListenableFuture to react to solve() exceptions immediately?
        solverFuture = executor.submit((SolvingTask) () -> solver.solve(solution));
    }

    void stopSolver() {
        if (solverFuture != null) {
            // TODO what happens if solver hasn't started yet (solve() is called asynchronously)
            solver.terminateEarly();
            // make sure solver has terminated and propagate exceptions
            try {
                solverFuture.get();
                solverFuture = null;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Failed to stop solver", e);
            } catch (ExecutionException e) {
                throw new RuntimeException("Failed to stop solver", e);
            }
        }
    }

    private void assertSolverIsAlive() {
        if (solverFuture == null) {
            throw new IllegalStateException("Solver has not started yet");
        }
        if (solverFuture.isDone()) {
            try {
                solverFuture.get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Solver has died", e);
            } catch (ExecutionException e) {
                throw new RuntimeException("Solver has died", e);
            }
            throw new IllegalStateException("Solver has finished solving even though it operates in daemon mode");
        }
    }

    void addLocation(Location location) {
        assertSolverIsAlive();
        solver.addProblemFactChange(new AddCustomer(CustomerFactory.customer(location)));
    }

    void removeLocation(Location location) {
        assertSolverIsAlive();
        solver.addProblemFactChanges(Arrays.asList(
                new RemoveCustomer(CustomerFactory.customer(location)),
                new RemoveLocation(location)
        ));
    }

    void addVehicle(Vehicle vehicle) {
        assertSolverIsAlive();
        solver.addProblemFactChange(new AddVehicle(vehicle));
    }

    void removeVehicle(Vehicle vehicle) {
        assertSolverIsAlive();
        solver.addProblemFactChange(new RemoveVehicle(vehicle));
    }

    interface SolvingTask extends Callable<VehicleRoutingSolution> {

    }
}
