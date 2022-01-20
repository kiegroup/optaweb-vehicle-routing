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

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.event.BestSolutionChangedEvent;
import org.optaplanner.core.api.solver.event.SolverEventListener;
import org.optaweb.vehiclerouting.plugin.planner.change.AddVehicle;
import org.optaweb.vehiclerouting.plugin.planner.change.AddVisit;
import org.optaweb.vehiclerouting.plugin.planner.change.ChangeVehicleCapacity;
import org.optaweb.vehiclerouting.plugin.planner.change.RemoveVehicle;
import org.optaweb.vehiclerouting.plugin.planner.change.RemoveVisit;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVehicle;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVisit;
import org.optaweb.vehiclerouting.plugin.planner.domain.VehicleRoutingSolution;
import org.optaweb.vehiclerouting.service.error.ErrorEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

/**
 * Manages a solver running in a different thread.
 * <p>
 * Does following:
 * <ul>
 * <li>Starts solver by running {@link Solver#solve(Object problem)} in a thread that's not the caller's thread.</li>
 * <li>Stops the solver (synchronously).</li>
 * <li>Adds problem fact changes to the solver.</li>
 * <li>Propagates any exception that happens in {@code Solver.solver()} (in a different thread) to the thread that
 * interacts with {@code SolverManager}.</li>
 * <li>Listens for best solution changes and publishes new best solutions via {@link RouteChangedEventPublisher}.</li>
 * </ul>
 */
@ApplicationScoped
@Default
class SolverManager implements SolverEventListener<VehicleRoutingSolution> {

    private static final Logger logger = LoggerFactory.getLogger(SolverManager.class);

    private final Solver<VehicleRoutingSolution> solver;
    private final ListeningExecutorService executor;
    private final RouteChangedEventPublisher routeChangedEventPublisher;
    private final Event<ErrorEvent> errorEvent;

    private ListenableFuture<VehicleRoutingSolution> solverFuture;

    @Inject
    SolverManager(
            Solver<VehicleRoutingSolution> solver,
            ListeningExecutorService executor,
            RouteChangedEventPublisher routeChangedEventPublisher,
            Event<ErrorEvent> errorEvent) {
        this.solver = solver;
        this.executor = executor;
        this.routeChangedEventPublisher = routeChangedEventPublisher;
        this.errorEvent = errorEvent;
        this.solver.addEventListener(this);
    }

    @Override
    public void bestSolutionChanged(BestSolutionChangedEvent<VehicleRoutingSolution> bestSolutionChangedEvent) {
        // CAUTION! This runs on the solver thread. Implications:
        // 1. The method should be as quick as possible to avoid blocking solver unnecessarily.
        // 2. This place is a potential source of race conditions.
        if (!bestSolutionChangedEvent.isEveryProblemChangeProcessed()) {
            logger.info("Ignoring a new best solution that has some problem facts missing");
            return;
        }
        // TODO Race condition, if a servlet thread deletes that location in the middle of this method happening
        //      on the solver thread. Make sure that location is still in the repository.
        //      Maybe repair the solution OR ignore if it's inconsistent (log a WARNING).
        routeChangedEventPublisher.publishSolution(bestSolutionChangedEvent.getNewBestSolution()); // TODO @Async
    }

    void startSolver(VehicleRoutingSolution solution) {
        if (solverFuture != null) {
            throw new IllegalStateException("Solver start has already been requested");
        }
        solverFuture = executor.submit((SolvingTask) () -> solver.solve(solution));
        solverFuture.addListener(
                // IMPORTANT: This is happening on the solver thread.
                // TODO maybe restart or somehow recover?
                () -> {
                    if (!solver.isTerminateEarly()) {
                        // Solver in daemon mode can't return from solve() unless it has been terminated early
                        // (see #stopSolver()).
                        // So this case is only possible when an exception is thrown during solver.solve().
                        try {
                            solverFuture.get();
                            logger.error("The solver has stopped without being terminated early so at this point"
                                    + " it is expected to have crashed but there was no exception.\n"
                                    + "If you see this other than during test execution it is probably a bug.");
                            errorEvent.fire(new ErrorEvent(
                                    this,
                                    "Solver stopped without being terminated early and without throwing an exception."
                                            + " This is a bug."));
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            throw new RuntimeException("Interrupted while retrieving the cause of solver failure", e);
                        } catch (ExecutionException e) {
                            logger.error("Solver failed", e);
                            errorEvent.fire(new ErrorEvent(this, e.toString()));
                        }
                    }
                },
                MoreExecutors.directExecutor());
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
                // Skipping the wrapper ExecutionException because it only tells that the problem occurred
                // in solverFuture.get() but that's obvious.
                throw new RuntimeException("Failed to stop solver", e.getCause());
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
                // Skipping the wrapper ExecutionException because it only tells that the problem occurred
                // in solverFuture.get() but that's obvious.
                throw new RuntimeException("Solver has died", e.getCause());
            }
            throw new IllegalStateException("Solver has finished solving even though it operates in daemon mode");
        }
    }

    void addVisit(PlanningVisit visit) {
        assertSolverIsAlive();
        solver.addProblemChange(new AddVisit(visit));
    }

    void removeVisit(PlanningVisit visit) {
        assertSolverIsAlive();
        solver.addProblemChange(new RemoveVisit(visit));
    }

    void addVehicle(PlanningVehicle vehicle) {
        assertSolverIsAlive();
        solver.addProblemChange(new AddVehicle(vehicle));
    }

    void removeVehicle(PlanningVehicle vehicle) {
        assertSolverIsAlive();
        solver.addProblemChange(new RemoveVehicle(vehicle));
    }

    void changeCapacity(PlanningVehicle vehicle) {
        assertSolverIsAlive();
        solver.addProblemChange(new ChangeVehicleCapacity(vehicle));
    }

    /**
     * An alias interface that fixates the Callable's type parameter. This avoids unchecked warnings in tests.
     */
    interface SolvingTask extends Callable<VehicleRoutingSolution> {

    }
}
