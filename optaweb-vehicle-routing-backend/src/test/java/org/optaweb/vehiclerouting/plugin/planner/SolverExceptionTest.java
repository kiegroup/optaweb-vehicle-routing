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

import java.util.concurrent.FutureTask;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.optaplanner.core.api.solver.Solver;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVehicle;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVehicleFactory;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVisit;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVisitFactory;
import org.optaweb.vehiclerouting.plugin.planner.domain.SolutionFactory;
import org.optaweb.vehiclerouting.plugin.planner.domain.VehicleRoutingSolution;
import org.springframework.core.task.AsyncTaskExecutor;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SolverExceptionTest {

    @Mock
    private Solver<VehicleRoutingSolution> solver;
    @Mock
    private AsyncTaskExecutor executor;
    @InjectMocks
    private SolverManager solverManager;

    @Test
    void should_propagate_any_exception_from_solver() {
        // arrange
        // Prepare a future that will be returned by mock executor
        FutureTask<VehicleRoutingSolution> task = new FutureTask<>(() -> {
            throw new TestException();
        }, null);
        when(executor.submit(any(SolverManager.SolvingTask.class))).thenReturn(task);
        // Run it synchronously (otherwise the test would be unreliable!)
        task.run();
        solverManager.startSolver(SolutionFactory.emptySolution());

        PlanningVisit planningVisit = PlanningVisitFactory.testVisit(1);
        PlanningVehicle planningVehicle = PlanningVehicleFactory.testVehicle(1);

        // act & assert
        assertTestExceptionThrownDuringOperation(() -> solverManager.addVisit(planningVisit));
        assertTestExceptionThrownDuringOperation(() -> solverManager.removeVisit(planningVisit));
        assertTestExceptionThrownDuringOperation(() -> solverManager.addVehicle(planningVehicle));
        assertTestExceptionThrownDuringOperation(() -> solverManager.removeVehicle(planningVehicle));

        assertTestExceptionThrownWhenStoppingSolver(solverManager);
    }

    private static void assertTestExceptionThrownDuringOperation(ThrowingCallable runnable) {
        assertTestExceptionThrownDuring(runnable, "died");
    }

    private static void assertTestExceptionThrownWhenStoppingSolver(SolverManager routeOptimizer) {
        assertTestExceptionThrownDuring(routeOptimizer::stopSolver, "stop");
    }

    private static void assertTestExceptionThrownDuring(ThrowingCallable runnable, String message) {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(runnable)
                .withMessageContaining(message)
                .withCauseInstanceOf(TestException.class);
    }

    private static class TestException extends RuntimeException {

    }
}
