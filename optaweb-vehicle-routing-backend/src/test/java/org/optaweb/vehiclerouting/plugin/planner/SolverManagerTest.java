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

import java.util.List;
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
import org.optaplanner.core.impl.solver.ProblemFactChange;
import org.optaweb.vehiclerouting.plugin.planner.change.AddVehicle;
import org.optaweb.vehiclerouting.plugin.planner.change.AddVisit;
import org.optaweb.vehiclerouting.plugin.planner.change.ChangeVehicleCapacity;
import org.optaweb.vehiclerouting.plugin.planner.change.RemoveLocation;
import org.optaweb.vehiclerouting.plugin.planner.change.RemoveVehicle;
import org.optaweb.vehiclerouting.plugin.planner.change.RemoveVisit;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningLocation;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVehicle;
import org.optaweb.vehiclerouting.plugin.planner.domain.VehicleRoutingSolution;
import org.springframework.core.task.AsyncTaskExecutor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.AdditionalAnswers.answer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SolverManagerTest {

    @Captor
    private ArgumentCaptor<List<ProblemFactChange<VehicleRoutingSolution>>> problemFactChangeArgumentCaptor;
    @Captor
    private ArgumentCaptor<VehicleRoutingSolution> solutionArgumentCaptor;
    @Mock
    private BestSolutionChangedEvent<VehicleRoutingSolution> bestSolutionChangedEvent;
    @Mock
    private Future<VehicleRoutingSolution> solverFuture;

    @Mock
    private Solver<VehicleRoutingSolution> solver;
    @Mock
    private AsyncTaskExecutor executor;
    @Mock
    private SolutionPublisher solutionPublisher;
    @InjectMocks
    private SolverManager solverManager;

    private void returnSolverFutureWhenSolverIsStarted() {
        // always run the runnable submitted to executor (that's what every executor does)
        // we can then verify that solver.solve() has been called
        when(executor.submit(any(SolverManager.SolvingTask.class))).thenAnswer(
                answer((Answer1<Future<VehicleRoutingSolution>, SolverManager.SolvingTask>) callable -> {
                    callable.call();
                    return solverFuture;
                })
        );
    }

    @Test
    void should_listen_for_best_solution_events() {
        verify(solver).addEventListener(solverManager);
    }

    @Test
    void ignore_new_best_solutions_when_unprocessed_fact_changes() {
        // arrange
        when(bestSolutionChangedEvent.isEveryProblemFactChangeProcessed()).thenReturn(false);

        // act
        solverManager.bestSolutionChanged(bestSolutionChangedEvent);

        // assert
        verify(bestSolutionChangedEvent, never()).getNewBestSolution();
        verify(solutionPublisher, never()).publishSolution(any());
    }

    @Test
    void publish_new_best_solution_if_all_fact_changes_processed() {
        VehicleRoutingSolution solution = new VehicleRoutingSolution();
        when(bestSolutionChangedEvent.isEveryProblemFactChangeProcessed()).thenReturn(true);
        when(bestSolutionChangedEvent.getNewBestSolution()).thenReturn(solution);

        solverManager.bestSolutionChanged(bestSolutionChangedEvent);

        verify(solutionPublisher).publishSolution(solutionArgumentCaptor.capture());
        VehicleRoutingSolution event = solutionArgumentCaptor.getValue();
        assertThat(event).isSameAs(solution);
    }

    @Test
    void startSolver_should_start_solver() {
        returnSolverFutureWhenSolverIsStarted();
        solverManager.startSolver(mock(VehicleRoutingSolution.class));
        verify(solver).solve(any(VehicleRoutingSolution.class));

        // cannot start solver that is already solving
        assertThatIllegalStateException()
                .isThrownBy(() -> solverManager.startSolver(mock(VehicleRoutingSolution.class)));
    }

    @Test
    void stopSolver_should_terminate_solver() {
        returnSolverFutureWhenSolverIsStarted();
        solverManager.startSolver(mock(VehicleRoutingSolution.class));
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
        solverManager.startSolver(mock(VehicleRoutingSolution.class));
        when(solverFuture.isDone()).thenReturn(true);
        when(solverFuture.get()).thenThrow(InterruptedException.class);

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> solverManager.removeLocation(new PlanningLocation()));
        assertThat(Thread.interrupted()).isTrue();

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> solverManager.stopSolver());
        assertThat(Thread.interrupted()).isTrue();
    }

    @Test
    void change_operations_should_fail_if_solver_has_not_started_yet() {
        assertThatIllegalStateException()
                .isThrownBy(() -> solverManager.addVehicle(mock(PlanningVehicle.class)))
                .withMessageContaining("started");
        assertThatIllegalStateException()
                .isThrownBy(() -> solverManager.removeVehicle(mock(PlanningVehicle.class)))
                .withMessageContaining("started");
        assertThatIllegalStateException()
                .isThrownBy(() -> solverManager.changeCapacity(mock(PlanningVehicle.class)))
                .withMessageContaining("started");
        assertThatIllegalStateException()
                .isThrownBy(() -> solverManager.addLocation(mock(PlanningLocation.class)))
                .withMessageContaining("started");
        assertThatIllegalStateException()
                .isThrownBy(() -> solverManager.removeLocation(mock(PlanningLocation.class)))
                .withMessageContaining("started");
    }

    @Test
    void change_operations_should_fail_is_solver_has_died() throws ExecutionException, InterruptedException {
        returnSolverFutureWhenSolverIsStarted();
        solverManager.startSolver(mock(VehicleRoutingSolution.class));
        when(solverFuture.isDone()).thenReturn(true);
        when(solverFuture.get()).thenThrow(ExecutionException.class);

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> solverManager.addVehicle(mock(PlanningVehicle.class)))
                .withMessageContaining("died");
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> solverManager.removeVehicle(mock(PlanningVehicle.class)))
                .withMessageContaining("died");
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> solverManager.changeCapacity(mock(PlanningVehicle.class)))
                .withMessageContaining("died");
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> solverManager.addLocation(mock(PlanningLocation.class)))
                .withMessageContaining("died");
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> solverManager.removeLocation(mock(PlanningLocation.class)))
                .withMessageContaining("died");
    }

    @Test
    void change_operations_should_submit_problem_fact_changes_to_solver() {
        returnSolverFutureWhenSolverIsStarted();
        solverManager.startSolver(mock(VehicleRoutingSolution.class));
        when(solverFuture.isDone()).thenReturn(false);

        solverManager.addVehicle(mock(PlanningVehicle.class));
        verify(solver).addProblemFactChange(any(AddVehicle.class));

        solverManager.removeVehicle(mock(PlanningVehicle.class));
        verify(solver).addProblemFactChange(any(RemoveVehicle.class));

        solverManager.changeCapacity(mock(PlanningVehicle.class));
        verify(solver).addProblemFactChange(any(ChangeVehicleCapacity.class));

        solverManager.addLocation(mock(PlanningLocation.class));
        verify(solver).addProblemFactChange(any(AddVisit.class));

        solverManager.removeLocation(mock(PlanningLocation.class));
        verify(solver).addProblemFactChanges(problemFactChangeArgumentCaptor.capture());
        List<ProblemFactChange<VehicleRoutingSolution>> problemFactChanges = problemFactChangeArgumentCaptor.getValue();
        assertThat(problemFactChanges).hasSize(2);
        assertThat(problemFactChanges.get(0)).isInstanceOf(RemoveVisit.class);
        assertThat(problemFactChanges.get(1)).isInstanceOf(RemoveLocation.class);
    }
}
