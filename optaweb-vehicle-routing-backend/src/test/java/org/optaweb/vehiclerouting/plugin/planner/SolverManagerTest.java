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
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.AdditionalAnswers.answer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVisitFactory.testVisit;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer1;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.event.BestSolutionChangedEvent;
import org.optaweb.vehiclerouting.plugin.planner.change.AddVehicle;
import org.optaweb.vehiclerouting.plugin.planner.change.AddVisit;
import org.optaweb.vehiclerouting.plugin.planner.change.ChangeVehicleCapacity;
import org.optaweb.vehiclerouting.plugin.planner.change.RemoveVehicle;
import org.optaweb.vehiclerouting.plugin.planner.change.RemoveVisit;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVehicle;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVehicleFactory;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVisit;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVisitFactory;
import org.optaweb.vehiclerouting.plugin.planner.domain.SolutionFactory;
import org.optaweb.vehiclerouting.plugin.planner.domain.VehicleRoutingSolution;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

@ExtendWith(MockitoExtension.class)
class SolverManagerTest {

    private final VehicleRoutingSolution solution = SolutionFactory.emptySolution();
    private final PlanningVehicle testVehicle = PlanningVehicleFactory.testVehicle(1);
    private final PlanningVisit testVisit = PlanningVisitFactory.testVisit(1);

    @Captor
    private ArgumentCaptor<VehicleRoutingSolution> solutionArgumentCaptor;
    @Mock
    private BestSolutionChangedEvent<VehicleRoutingSolution> bestSolutionChangedEvent;
    @Mock
    private ListenableFuture<VehicleRoutingSolution> solverFuture;

    @Mock
    private Solver<VehicleRoutingSolution> solver;
    @Mock
    private ListeningExecutorService executor;
    @Mock
    private RouteChangedEventPublisher routeChangedEventPublisher;
    @InjectMocks
    private SolverManager solverManager;

    private void returnSolverFutureWhenSolverIsStarted() {
        // always run the runnable submitted to executor (that's what every executor does)
        // we can then verify that solver.solve() has been called
        when(executor.submit(any(SolverManager.SolvingTask.class))).thenAnswer(
                answer((Answer1<Future<VehicleRoutingSolution>, SolverManager.SolvingTask>) callable -> {
                    callable.call();
                    return solverFuture;
                }));
    }

    @Test
    void should_listen_for_best_solution_events() {
        verify(solver).addEventListener(solverManager);
    }

    @Test
    void ignore_new_best_solutions_when_unprocessed_fact_changes() {
        // arrange
        when(bestSolutionChangedEvent.isEveryProblemChangeProcessed()).thenReturn(false);

        // act
        solverManager.bestSolutionChanged(bestSolutionChangedEvent);

        // assert
        verify(bestSolutionChangedEvent, never()).getNewBestSolution();
        verify(routeChangedEventPublisher, never()).publishSolution(any());
    }

    @Test
    void publish_new_best_solution_if_all_fact_changes_processed() {
        VehicleRoutingSolution solution = SolutionFactory.emptySolution();
        when(bestSolutionChangedEvent.isEveryProblemChangeProcessed()).thenReturn(true);
        when(bestSolutionChangedEvent.getNewBestSolution()).thenReturn(solution);

        solverManager.bestSolutionChanged(bestSolutionChangedEvent);

        verify(routeChangedEventPublisher).publishSolution(solutionArgumentCaptor.capture());
        VehicleRoutingSolution event = solutionArgumentCaptor.getValue();
        assertThat(event).isSameAs(solution);
    }

    @Test
    void startSolver_should_start_solver() {
        returnSolverFutureWhenSolverIsStarted();
        solverManager.startSolver(solution);
        verify(solver).solve(solution);

        // cannot start solver that is already solving
        assertThatIllegalStateException()
                .isThrownBy(() -> solverManager.startSolver(solution));
    }

    @Test
    void stopSolver_should_terminate_solver() {
        returnSolverFutureWhenSolverIsStarted();
        solverManager.startSolver(solution);
        solverManager.stopSolver();
        verify(solver).terminateEarly();

        // another stopSolver() does nothing
        solverManager.stopSolver();
        // This verifies there were no more invocations of terminateEarly() without clearing all invocations.
        // Not using Mockito.clearInvocations() only because it doesn't like generic arguments.
        verify(solver).terminateEarly();
    }

    @Test
    void reset_interrupted_flag() throws ExecutionException, InterruptedException {
        returnSolverFutureWhenSolverIsStarted();
        // start solver
        solverManager.startSolver(solution);
        when(solverFuture.isDone()).thenReturn(true);
        when(solverFuture.get()).thenThrow(InterruptedException.class);

        PlanningVisit visit = testVisit(0);
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> solverManager.removeVisit(visit));
        assertThat(Thread.interrupted()).isTrue();

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> solverManager.stopSolver());
        assertThat(Thread.interrupted()).isTrue();
    }

    @Test
    void change_operations_should_fail_if_solver_has_not_started_yet() {
        assertThatIllegalStateException()
                .isThrownBy(() -> solverManager.addVehicle(testVehicle))
                .withMessageContaining("started");
        assertThatIllegalStateException()
                .isThrownBy(() -> solverManager.removeVehicle(testVehicle))
                .withMessageContaining("started");
        assertThatIllegalStateException()
                .isThrownBy(() -> solverManager.changeCapacity(testVehicle))
                .withMessageContaining("started");
        assertThatIllegalStateException()
                .isThrownBy(() -> solverManager.addVisit(testVisit))
                .withMessageContaining("started");
        assertThatIllegalStateException()
                .isThrownBy(() -> solverManager.removeVisit(testVisit))
                .withMessageContaining("started");
    }

    @Test
    void change_operations_should_fail_is_solver_has_died() throws ExecutionException, InterruptedException {
        returnSolverFutureWhenSolverIsStarted();
        solverManager.startSolver(solution);
        when(solverFuture.isDone()).thenReturn(true);
        when(solverFuture.get()).thenThrow(ExecutionException.class);

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> solverManager.addVehicle(testVehicle))
                .withMessageContaining("died");
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> solverManager.removeVehicle(testVehicle))
                .withMessageContaining("died");
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> solverManager.changeCapacity(testVehicle))
                .withMessageContaining("died");
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> solverManager.addVisit(testVisit))
                .withMessageContaining("died");
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> solverManager.removeVisit(testVisit))
                .withMessageContaining("died");
    }

    @Test
    void change_operations_should_submit_problem_fact_changes_to_solver() {
        returnSolverFutureWhenSolverIsStarted();
        solverManager.startSolver(solution);
        when(solverFuture.isDone()).thenReturn(false);

        solverManager.addVehicle(testVehicle);
        verify(solver).addProblemChange(any(AddVehicle.class));

        solverManager.removeVehicle(testVehicle);
        verify(solver).addProblemChange(any(RemoveVehicle.class));

        solverManager.changeCapacity(testVehicle);
        verify(solver).addProblemChange(any(ChangeVehicleCapacity.class));

        solverManager.addVisit(testVisit);
        verify(solver).addProblemChange(any(AddVisit.class));

        solverManager.removeVisit(testVisit);
        verify(solver).addProblemChange(any(RemoveVisit.class));
    }
}
