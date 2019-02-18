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
public class SolverTest {

    private static final Logger logger = LoggerFactory.getLogger(SolverTest.class);

    @Mock
    private Map<RoadLocation, Double> distanceMap;

    private SolverFactory<VehicleRoutingSolution> sf;
    private ExecutorService executor;
    private ProblemFactChangeProcessingMonitor monitor;

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

    @Ignore
    @Test
    public void solver_in_daemon_mode_should_not_fail_on_empty_solution() {
        sf.getSolverConfig().setDaemon(true);
        sf.buildSolver().solve(RouteOptimizerImpl.emptySolution());
    }

    @Test
    public void removing_customers_should_not_fail() throws InterruptedException, ExecutionException {
        VehicleRoutingSolution solution = RouteOptimizerImpl.emptySolution();
        RouteOptimizerImpl.addDepot(solution, location(1));
        RouteOptimizerImpl.addCustomer(solution, location(2));

        sf.getSolverConfig().setDaemon(true);
        Solver<VehicleRoutingSolution> solver = sf.buildSolver();
        solver.addEventListener(monitor);
        Future<VehicleRoutingSolution> futureSolution = executor.submit(() -> solver.solve(solution));

        for (int id = 3; id < 6; id++) {
            logger.info("Add customer ({})", id);
            monitor.startProblemFactChange();
            solver.addProblemFactChange(new AddCustomer(location(id)));
            assertThat(monitor.awaitProblemFactChangeCompletion(1000)).isTrue();
        }

        List<Integer> integers = Arrays.asList(5, 2, 3);
        for (int id : integers) {
            logger.info("Remove customer ({})", id);
            Location removeLocation = location(id);
            assertThat(solver.isEveryProblemFactChangeProcessed()).isTrue();
            monitor.startProblemFactChange();
            solver.addProblemFactChanges(Arrays.asList(new RemoveCustomer(removeLocation), new RemoveLocation(removeLocation)));
            assertThat(solver.isEveryProblemFactChangeProcessed()).isFalse();
            if (!monitor.awaitProblemFactChangeCompletion(1000)) {
                assertThat(futureSolution.get()).isNotNull();
                fail("Problem fact change hasn't been completed.");
            }
        }

        solver.terminateEarly();
        assertThat(futureSolution.get()).isNotNull();
    }

    private Location location(long id) {
        RoadLocation location = new RoadLocation();
        location.setId(id);
        location.setTravelDistanceMap(distanceMap);
        return location;
    }

    static class ProblemFactChangeProcessingMonitor implements SolverEventListener<VehicleRoutingSolution> {

        private static final Logger logger = LoggerFactory.getLogger(ProblemFactChangeProcessingMonitor.class);

        private Semaphore problemFactChanges = new Semaphore(0);

        void startProblemFactChange() {
            problemFactChanges.drainPermits();
            problemFactChanges.tryAcquire();
        }

        boolean awaitProblemFactChangeCompletion(int milliseconds) throws InterruptedException {
            logger.debug("WAIT", problemFactChanges.availablePermits(), problemFactChanges.getQueueLength());
            if (problemFactChanges.tryAcquire(milliseconds, TimeUnit.MILLISECONDS)) {
                logger.info("Problem Fact Change DONE");
                return true;
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
