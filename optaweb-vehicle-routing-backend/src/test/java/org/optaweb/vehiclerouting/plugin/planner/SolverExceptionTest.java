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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import javax.enterprise.event.Event;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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
import org.optaweb.vehiclerouting.service.error.ErrorEvent;

import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.ListeningExecutorService;

@ExtendWith(MockitoExtension.class)
class SolverExceptionTest {

    @Mock
    private Solver<VehicleRoutingSolution> solver;
    @Mock
    private ListeningExecutorService executor;
    @Mock
    private Event<ErrorEvent> eventPublisher;
    @Captor
    ArgumentCaptor<ErrorEvent> errorEventArgumentCaptor;
    @InjectMocks
    private SolverManager solverManager;

    @Test
    void should_publish_error_if_solver_stops_solving_without_being_terminated() {
        // arrange
        // Prepare a future that will be returned by mock executor
        ListenableFutureTask<VehicleRoutingSolution> task = ListenableFutureTask.create(SolutionFactory::emptySolution);
        when(executor.submit(any(SolverManager.SolvingTask.class))).thenReturn(task);
        // Run it synchronously (otherwise the test would be unreliable!)
        task.run();

        // act
        solverManager.startSolver(SolutionFactory.emptySolution());

        // assert
        verify(eventPublisher).fire(errorEventArgumentCaptor.capture());
        assertThat(errorEventArgumentCaptor.getValue().message).contains("This is a bug.");
    }

    @Test
    void should_not_publish_error_if_solver_is_terminated_early() {
        // arrange
        // Prepare a future that will be returned by mock executor
        ListenableFutureTask<VehicleRoutingSolution> task = ListenableFutureTask.create(SolutionFactory::emptySolution);
        when(executor.submit(any(SolverManager.SolvingTask.class))).thenReturn(task);
        // Pretend the solver has been terminated by stopSolver()...
        when(solver.isTerminateEarly()).thenReturn(true);

        // act
        solverManager.startSolver(SolutionFactory.emptySolution());
        task.run(); // ...so that when this invokes the success callback, it won't publish an error

        // assert
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void should_propagate_any_exception_from_solver() {
        // arrange
        // Prepare a future that will be returned by mock executor
        String exceptionMessage = "msg 123";
        ListenableFutureTask<VehicleRoutingSolution> task = ListenableFutureTask.create(() -> {
            throw new TestException(exceptionMessage);
        });
        when(executor.submit(any(SolverManager.SolvingTask.class))).thenReturn(task);
        // act (1)
        // Run it synchronously (otherwise the test would be unreliable!)
        task.run();
        solverManager.startSolver(SolutionFactory.emptySolution());

        // assert (1)
        verify(eventPublisher).fire(errorEventArgumentCaptor.capture());
        assertThat(errorEventArgumentCaptor.getValue().message)
                .contains(TestException.class.getName())
                .contains(exceptionMessage);

        PlanningVisit planningVisit = PlanningVisitFactory.testVisit(1);
        PlanningVehicle planningVehicle = PlanningVehicleFactory.testVehicle(1);

        // act & assert (2)
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

        TestException(String message) {
            super(message);
        }
    }
}
