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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.event.BestSolutionChangedEvent;
import org.optaplanner.core.api.solver.event.SolverEventListener;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaweb.vehiclerouting.plugin.planner.change.AddVisit;
import org.optaweb.vehiclerouting.plugin.planner.change.RemoveLocation;
import org.optaweb.vehiclerouting.plugin.planner.change.RemoveVisit;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningDepot;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningLocation;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVehicle;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVehicleFactory;
import org.optaweb.vehiclerouting.plugin.planner.domain.VehicleRoutingSolution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVisitFactory.fromLocation;
import static org.optaweb.vehiclerouting.plugin.planner.domain.SolutionFactory.emptySolution;
import static org.optaweb.vehiclerouting.plugin.planner.domain.SolutionFactory.solutionFromLocations;

@ExtendWith(MockitoExtension.class)
class SolverIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(SolverIntegrationTest.class);

    @Mock
    private Map<PlanningLocation, Double> distanceMap;

    private SolverConfig solverConfig;
    private ExecutorService executor;
    private ProblemFactChangeProcessingMonitor monitor;
    private Future<VehicleRoutingSolution> futureSolution;

    @BeforeEach
    void setUp() {
        solverConfig = SolverConfig.createFromXmlResource(Constants.SOLVER_CONFIG);
        solverConfig.setDaemon(true);
        executor = Executors.newSingleThreadExecutor();
        monitor = new ProblemFactChangeProcessingMonitor();
        when(distanceMap.get(any(PlanningLocation.class))).thenReturn(1.0);
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.SECONDS);
    }

    @Disabled("Solver fails fast on empty value ranges") // TODO file an OptaPlanner ticket for empty value ranges
    @Test
    void solver_in_daemon_mode_should_not_fail_on_empty_solution() {
        Solver<VehicleRoutingSolution> solver
                = SolverFactory.<VehicleRoutingSolution>create(solverConfig).buildSolver();
        assertThat(solver.solve(emptySolution())).isNotNull();
    }

    // TODO remove vehicle, change capacity, change demand...

    @Test
    void removing_visits_should_not_fail() {
        PlanningVehicle vehicle = PlanningVehicleFactory.testVehicle(1);
        VehicleRoutingSolution solution = solutionFromLocations(
                singletonList(vehicle),
                new PlanningDepot(location(1)),
                singletonList(location(2))
        );

        Solver<VehicleRoutingSolution> solver
                = SolverFactory.<VehicleRoutingSolution>create(solverConfig).buildSolver();
        solver.addEventListener(monitor);
        startSolver(solver, solution);

        for (int id = 3; id < 6; id++) {
            logger.info("Add visit ({})", id);
            monitor.beforeProblemFactChange();
            solver.addProblemFactChange(new AddVisit(fromLocation(location(id))));
            assertThat(monitor.awaitAllProblemFactChanges(1000)).isTrue();
        }

        List<Integer> visitIds = Arrays.asList(5, 2, 3);
        for (int id : visitIds) {
            logger.info("Remove visit ({})", id);
            assertThat(solver.isEveryProblemFactChangeProcessed()).isTrue();
            monitor.beforeProblemFactChange();
            solver.addProblemFactChanges(Arrays.asList(
                    new RemoveVisit(fromLocation(location(id))),
                    new RemoveLocation(location(id))
            ));
            assertThat(solver.isEveryProblemFactChangeProcessed()).isFalse(); // probably not 100% safe
            // Notice that it's not possible to check individual problem fact changes completion.
            // When we receive a BestSolutionChangedEvent with unprocessed PFCs,
            // we don't know how many of them there are.
            if (!monitor.awaitAllProblemFactChanges(1000)) {
                assertThat(terminateSolver(solver)).isNotNull();
                fail("Problem fact change hasn't been completed");
            }
        }

        assertThat(terminateSolver(solver)).isNotNull();
    }

    private void startSolver(Solver<VehicleRoutingSolution> solver, VehicleRoutingSolution solution) {
        futureSolution = executor.submit(() -> solver.solve(solution));
    }

    private VehicleRoutingSolution terminateSolver(Solver<VehicleRoutingSolution> solver) {
        solver.terminateEarly();
        try {
            return futureSolution.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("Interrupted", e);
        } catch (ExecutionException e) {
            fail("Solving failed", e);
        }
        throw new AssertionError();
    }

    private PlanningLocation location(long id) {
        PlanningLocation location = new PlanningLocation(id, 0, 0);
        location.setTravelDistanceMap(distanceMap);
        return location;
    }

    static class ProblemFactChangeProcessingMonitor implements SolverEventListener<VehicleRoutingSolution> {

        private static final Logger logger = LoggerFactory.getLogger(ProblemFactChangeProcessingMonitor.class);

        private final Semaphore problemFactChanges = new Semaphore(0);

        void beforeProblemFactChange() {
            int permitsDrained = problemFactChanges.drainPermits();
            logger.debug("Before PFC (permits drained: {})", permitsDrained);
        }

        boolean awaitAllProblemFactChanges(int milliseconds) {
            // Available permits may rarely be > 0 if the PFC completes before we start waiting,
            // or if the solution has improved since we called beforePFC() => the test is not completely reliable.
            logger.debug("WAIT (completed PFCs: {})", problemFactChanges.availablePermits());
            try {
                if (problemFactChanges.tryAcquire(milliseconds, TimeUnit.MILLISECONDS)) {
                    logger.info("Problem Fact Change DONE");
                    return true;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                fail("Interrupted", e);
            }
            return false;
        }

        @Override
        public void bestSolutionChanged(BestSolutionChangedEvent<VehicleRoutingSolution> event) {
            // This happens on solver thread
            if (!event.isEveryProblemFactChangeProcessed()) {
                logger.debug("UNPROCESSED");
            } else if (!event.getNewBestScore().isSolutionInitialized()) {
                logger.debug("UNINITIALIZED ({})", event.getNewBestScore());
            } else {
                logger.debug("New best solution (COMPLETE)");
                problemFactChanges.release();
            }
        }
    }
}
