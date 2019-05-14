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

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.event.BestSolutionChangedEvent;
import org.optaplanner.core.api.solver.event.SolverEventListener;
import org.optaplanner.examples.vehiclerouting.app.VehicleRoutingApp;
import org.optaplanner.examples.vehiclerouting.domain.Depot;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.domain.location.Location;
import org.optaplanner.examples.vehiclerouting.domain.location.RoadLocation;
import org.optaweb.vehiclerouting.plugin.planner.change.AddCustomer;
import org.optaweb.vehiclerouting.plugin.planner.change.RemoveCustomer;
import org.optaweb.vehiclerouting.plugin.planner.change.RemoveLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SolverIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(SolverIntegrationTest.class);

    @Mock
    private Map<RoadLocation, Double> distanceMap;

    private SolverFactory<VehicleRoutingSolution> sf;
    private ExecutorService executor;
    private ProblemFactChangeProcessingMonitor monitor;
    private Future<VehicleRoutingSolution> futureSolution;

    @Before
    public void setUp() {
        sf = SolverFactory.createFromXmlResource(VehicleRoutingApp.SOLVER_CONFIG);
        executor = Executors.newSingleThreadExecutor();
        monitor = new ProblemFactChangeProcessingMonitor();
        when(distanceMap.get(any(RoadLocation.class))).thenReturn(1.0);
    }

    @After
    public void tearDown() throws InterruptedException {
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.SECONDS);
    }

    @Ignore("Solver fails fast on empty value ranges") // TODO file an OptaPlanner ticket for empty value ranges
    @Test
    public void solver_in_daemon_mode_should_not_fail_on_empty_solution() {
        sf.getSolverConfig().setDaemon(true);
        assertThat(sf.buildSolver().solve(SolutionUtil.emptySolution())).isNotNull();
    }

    @Test
    public void removing_customers_should_not_fail() {
        VehicleRoutingSolution solution = SolutionUtil.emptySolution();
        Depot depot = SolutionUtil.addDepot(solution, location(1));
        SolutionUtil.addVehicle(solution, 1);
        SolutionUtil.moveAllVehiclesTo(solution, depot);
        SolutionUtil.addCustomer(solution, location(2));

        sf.getSolverConfig().setDaemon(true);
        Solver<VehicleRoutingSolution> solver = sf.buildSolver();
        solver.addEventListener(monitor);
        startSolver(solver, solution);

        for (int id = 3; id < 6; id++) {
            logger.info("Add customer ({})", id);
            monitor.beforeProblemFactChange();
            solver.addProblemFactChange(new AddCustomer(location(id)));
            assertThat(monitor.awaitAllProblemFactChanges(1000)).isTrue();
        }

        List<Integer> customerIds = Arrays.asList(5, 2, 3);
        for (int id : customerIds) {
            logger.info("Remove customer ({})", id);
            Location removeLocation = location(id);
            assertThat(solver.isEveryProblemFactChangeProcessed()).isTrue();
            monitor.beforeProblemFactChange();
            solver.addProblemFactChanges(Arrays.asList(
                    new RemoveCustomer(removeLocation),
                    new RemoveLocation(removeLocation)
            ));
            assertThat(solver.isEveryProblemFactChangeProcessed()).isFalse(); // probably not 100% safe
            // Notice that it's not possible to check individual problem fact changes completion.
            // When we receive a BestSolutionChangedEvent with unprocessed PFCs,
            // we don't know how many of them there are.
            if (!monitor.awaitAllProblemFactChanges(1000)) {
                assertThat(terminateSolver(solver)).isNotNull();
                fail("Problem fact change hasn't been completed.");
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

    private Location location(long id) {
        RoadLocation location = new RoadLocation();
        location.setId(id);
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
