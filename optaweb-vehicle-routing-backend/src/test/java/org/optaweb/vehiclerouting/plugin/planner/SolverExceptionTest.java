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

import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.examples.tsp.domain.TspSolution;
import org.optaweb.vehiclerouting.domain.LatLng;
import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.service.location.DistanceMatrix;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.task.AsyncTaskExecutor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SolverExceptionTest {

    private final Location location1 = new Location(1, LatLng.valueOf(1.0, 0.1));
    private final Location location2 = new Location(2, LatLng.valueOf(0.2, 2.2));
    private final Location location3 = new Location(3, LatLng.valueOf(3.4, 5.6));

    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private Solver<TspSolution> solver;
    @Mock
    private DistanceMatrix distanceMatrix;
    @Mock
    private AsyncTaskExecutor executor;
    @InjectMocks
    private RouteOptimizerImpl routeOptimizer;

    @Before
    public void setUp() {
        // Prepare a future that will be returned by mock executor
        FutureTask<Void> task = new FutureTask<>(() -> {
            throw new TestException();
        }, null);
        when(executor.submit(any(Runnable.class))).thenReturn((Future) task);
        // Run it synchronously (otherwise the test would be unreliable!)
        task.run();
    }

    @Test
    public void should_propagate_any_exception_from_solver() {
        routeOptimizer.addLocation(location1, distanceMatrix);
        assertThat(routeOptimizer.isSolving()).isFalse();
        routeOptimizer.addLocation(location2, distanceMatrix);

        assertTestExceptionThrownDuringOperation(() -> routeOptimizer.isSolving());
        assertTestExceptionThrownDuringOperation(() -> routeOptimizer.addLocation(location3, distanceMatrix));
        assertTestExceptionThrownDuringOperation(() -> routeOptimizer.removeLocation(location2));

        assertTestExceptionThrownWhenStoppingSolver(routeOptimizer);
    }

    private static void assertTestExceptionThrownDuringOperation(ThrowingCallable runnable) {
        assertTestExceptionThrownDuring(runnable, "died");
    }

    private static void assertTestExceptionThrownWhenStoppingSolver(RouteOptimizerImpl routeOptimizer) {
        assertTestExceptionThrownDuring(routeOptimizer::stopSolver, "stop");
    }

    private static void assertTestExceptionThrownDuring(ThrowingCallable runnable, String message) {
        assertThatThrownBy(runnable)
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining(message)
                .hasRootCauseInstanceOf(TestException.class);
    }

    private static class TestException extends RuntimeException {

    }
}
