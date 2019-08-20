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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.mockito.stubbing.Answer;
import org.mockito.stubbing.Answer1;
import org.mockito.stubbing.VoidAnswer1;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.event.BestSolutionChangedEvent;
import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.Customer;
import org.optaweb.vehiclerouting.domain.Depot;
import org.optaweb.vehiclerouting.domain.LocationNew;
import org.optaweb.vehiclerouting.domain.Standstill;
import org.optaweb.vehiclerouting.domain.location.RoadLocation;
import org.optaweb.vehiclerouting.service.location.DistanceMatrix;
import org.optaweb.vehiclerouting.service.route.RouteChangedEvent;
import org.optaweb.vehiclerouting.service.route.ShallowRoute;
import org.optaweb.vehiclerouting.solver.VehicleRoutingSolution;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.task.AsyncTaskExecutor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.AdditionalAnswers.answer;
import static org.mockito.AdditionalAnswers.answerVoid;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.optaweb.vehiclerouting.plugin.planner.SolutionUtil.planningLocation;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class RouteOptimizerImplTest {

    private final LocationNew locationNew1 = new LocationNew(1, Coordinates.valueOf(1.0, 0.1));
    private final LocationNew locationNew2 = new LocationNew(2, Coordinates.valueOf(0.2, 2.2));
    private final LocationNew locationNew3 = new LocationNew(3, Coordinates.valueOf(3.4, 5.6));

    private boolean isSolving;

    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private Solver<VehicleRoutingSolution> solver;
    @Mock
    private BestSolutionChangedEvent<VehicleRoutingSolution> bestSolutionChangedEvent;
    @Captor
    private ArgumentCaptor<RouteChangedEvent> routeChangedEventArgumentCaptor;
    @Mock
    private DistanceMatrix distanceMatrix;
    @Mock
    private AsyncTaskExecutor executor;
    @Mock
    private Future<VehicleRoutingSolution> solverFuture;
    @InjectMocks
    private RouteOptimizerImpl routeOptimizer;

    @BeforeEach
    void setUp() {
        // always run the runnable submitted to executor (that's what every executor does)
        // we can then verify that org.optaweb.vehiclerouting.solver.solve() has been called
        when(executor.submit(any(RouteOptimizerImpl.SolvingTask.class))).thenAnswer(
                answer((Answer1<Future<VehicleRoutingSolution>, RouteOptimizerImpl.SolvingTask>) callable -> {
                    callable.call();
                    return solverFuture;
                })
        );

        // mimic solve() => isSolving(); terminateEarly() => !isSolving()
        isSolving = false;
        when(solver.isSolving()).thenAnswer((Answer<Boolean>) invocation -> isSolving);
        when(solver.solve(any())).thenAnswer(
                answerVoid((VoidAnswer1<VehicleRoutingSolution>) solution -> isSolving = true)
        );
        when(solver.terminateEarly()).thenAnswer((Answer<Boolean>) invocation -> {
            isSolving = false;
            return true;
        });
    }

    @Test
    void should_listen_for_best_solution_events() {
        verify(solver).addEventListener(routeOptimizer);
    }

    @Test
    void ignore_new_best_solutions_when_unprocessed_fact_changes() {
        // arrange
        when(bestSolutionChangedEvent.isEveryProblemFactChangeProcessed()).thenReturn(false);

        // act
        routeOptimizer.bestSolutionChanged(bestSolutionChangedEvent);

        // assert
        verify(bestSolutionChangedEvent, never()).getNewBestSolution();
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void publish_new_best_solution_if_all_fact_changes_processed() {
        VehicleRoutingSolution solution = createSolution(locationNew1, locationNew2);
        when(bestSolutionChangedEvent.isEveryProblemFactChangeProcessed()).thenReturn(true);
        when(bestSolutionChangedEvent.getNewBestSolution()).thenReturn(solution);

        routeOptimizer.bestSolutionChanged(bestSolutionChangedEvent);

        verify(eventPublisher).publishEvent(routeChangedEventArgumentCaptor.capture());
        RouteChangedEvent event = routeChangedEventArgumentCaptor.getValue();

        assertThat(event.depot()).contains(locationNew1.id());
        assertThat(event.routes()).isNotEmpty();
        for (ShallowRoute route : event.routes()) {
            assertThat(route.depotId).isEqualTo(locationNew1.id());
            assertThat(route.visitIds).containsExactly(locationNew2.id());
        }
    }

    @Test
    void solution_with_depot_and_no_visits_should_be_published() {
        routeOptimizer.addLocation(locationNew1, distanceMatrix);

        verify(eventPublisher).publishEvent(routeChangedEventArgumentCaptor.capture());
        RouteChangedEvent event = routeChangedEventArgumentCaptor.getValue();

        assertThat(solver.isSolving()).isFalse();
        assertThat(event.depot()).contains(locationNew1.id());
        assertThat(event.routes()).hasSameSizeAs(SolutionUtil.initialSolution().getVehicleList());
        for (ShallowRoute route : event.routes()) {
            assertThat(route.depotId).isEqualTo(locationNew1.id());
            assertThat(route.visitIds).isEmpty();
        }
    }

    @Test
    void solver_should_start_when_two_locations_added() {
        // add 2 locations
        routeOptimizer.addLocation(locationNew1, distanceMatrix);
        routeOptimizer.addLocation(locationNew2, distanceMatrix);

        assertThat(isSolving).isTrue();
        assertThat(solver.isSolving()).isTrue();
        // solving has started after adding a second location (depot + visit)
        verify(solver).solve(any());

        // problem fact change only happens when adding location to a running org.optaweb.vehiclerouting.solver
        // or when more than 1 location remains after removing one
        verify(solver, never()).addProblemFactChange(any());
    }

    @Test
    void solver_should_stop_when_locations_reduced_to_one() throws ExecutionException, InterruptedException {
        // add 2 locations
        routeOptimizer.addLocation(locationNew1, distanceMatrix);
        routeOptimizer.addLocation(locationNew2, distanceMatrix);

        // remove 1 location from running org.optaweb.vehiclerouting.solver
        assertThat(solver.isSolving()).isTrue();
        routeOptimizer.removeLocation(locationNew2);

        assertThat(solver.isSolving()).isFalse();
        verify(solver).terminateEarly();
        verify(solverFuture).get();

        // problem fact change only happens when adding location to a running org.optaweb.vehiclerouting.solver
        // or when more than 1 location remains after removing one
        verify(solver, never()).addProblemFactChange(any());
    }

    @Test
    void solution_update_event_should_only_have_empty_routes_when_last_visit_removed() {
        // FIXME This test shouldn't be needed. This is a problem with bad encapsulation of the planning domain in
        //   optaplanner-examples. Once we introduce our own planning domain with a better API, the test should be
        //   replaced/simplified/removed.

        // Prepare a solution with 1 depot, 2 vehicles, 1 customer and both vehicles visiting to that customer
        VehicleRoutingSolution solution = SolutionUtil.emptySolution();
        Depot depot = SolutionUtil.addDepot(solution, planningLocation(locationNew1));
        SolutionUtil.addVehicle(solution, 1);
        SolutionUtil.addVehicle(solution, 2);
        SolutionUtil.moveAllVehiclesTo(solution, depot);
        Customer customer = SolutionUtil.addCustomer(solution, planningLocation(locationNew2));
        solution.getVehicleList().forEach(vehicle -> vehicle.setNextCustomer(customer));
        assertThat(SolutionUtil.routes(solution)).allMatch(shallowRoute -> shallowRoute.visitIds.size() == 1);
        solution.setScore(HardSoftLongScore.ofSoft(-1000)); // set non-zero travel distance

        // Start org.optaweb.vehiclerouting.solver by adding two locations
        routeOptimizer.addLocation(locationNew1, distanceMatrix);
        routeOptimizer.addLocation(locationNew2, distanceMatrix);

        // Pretend org.optaweb.vehiclerouting.solver found a new best best solution
        when(bestSolutionChangedEvent.isEveryProblemFactChangeProcessed()).thenReturn(true);
        when(bestSolutionChangedEvent.getNewBestSolution()).thenReturn(solution);
        routeOptimizer.bestSolutionChanged(bestSolutionChangedEvent);

        clearInvocations(eventPublisher);

        routeOptimizer.removeLocation(locationNew2);
        assertThat(routeOptimizer.isSolving()).isFalse();

        verify(eventPublisher).publishEvent(routeChangedEventArgumentCaptor.capture());
        RouteChangedEvent event = routeChangedEventArgumentCaptor.getValue();

        // no customer -> all routes should be empty
        assertThat(event.distance()).isEqualTo("0h 0m 0s"); // expect zero travel distance
        assertThat(event.depot()).isPresent();
        assertThat(event.routes()).hasSameSizeAs(solution.getVehicleList());
        for (ShallowRoute route : event.routes()) {
            assertThat(route.visitIds).isEmpty();
        }
    }

    @Test
    void removing_depot_impossible_when_there_are_other_locations() {
        // add 2 locations
        routeOptimizer.addLocation(locationNew1, distanceMatrix);
        routeOptimizer.addLocation(locationNew2, distanceMatrix);

        assertThatIllegalStateException()
                .isThrownBy(() -> routeOptimizer.removeLocation(locationNew1))
                .withMessageContaining("depot");
    }

    @Test
    void adding_location_to_running_solver_must_happen_through_problem_fact_change() {
        routeOptimizer.addLocation(locationNew1, distanceMatrix);
        assertThat(solver.isSolving()).isFalse();
        routeOptimizer.addLocation(locationNew2, distanceMatrix);
        assertThat(solver.isSolving()).isTrue();
        routeOptimizer.addLocation(locationNew3, distanceMatrix);
        verify(solver).addProblemFactChange(any());
    }

    @Test
    void removing_location_from_solver_with_more_than_two_locations_must_happen_through_problem_fact_change() {
        // set up a situation where org.optaweb.vehiclerouting.solver is running with 1 depot and 2 visits
        VehicleRoutingSolution solution = createSolution(locationNew1, locationNew2, locationNew3);
        when(bestSolutionChangedEvent.isEveryProblemFactChangeProcessed()).thenReturn(true);
        when(bestSolutionChangedEvent.getNewBestSolution()).thenReturn(solution);
        routeOptimizer.addLocation(locationNew1, distanceMatrix);
        routeOptimizer.addLocation(locationNew2, distanceMatrix);
        routeOptimizer.addLocation(locationNew3, distanceMatrix);
        routeOptimizer.bestSolutionChanged(bestSolutionChangedEvent);

        routeOptimizer.removeLocation(locationNew2);
        verify(solver).addProblemFactChanges(any()); // note that it's plural
        // org.optaweb.vehiclerouting.solver still running
        verify(solver, never()).terminateEarly();
    }

    @Test
    void clear_should_stop_solver_and_publish_initial_solution() throws ExecutionException, InterruptedException {
        // set up a situation where org.optaweb.vehiclerouting.solver is running with 1 depot and 2 visits
        VehicleRoutingSolution solution = createSolution(locationNew1, locationNew2, locationNew3);
        when(bestSolutionChangedEvent.isEveryProblemFactChangeProcessed()).thenReturn(true);
        when(bestSolutionChangedEvent.getNewBestSolution()).thenReturn(solution);
        routeOptimizer.addLocation(locationNew1, distanceMatrix);
        routeOptimizer.addLocation(locationNew2, distanceMatrix);
        routeOptimizer.addLocation(locationNew3, distanceMatrix);
        routeOptimizer.bestSolutionChanged(bestSolutionChangedEvent);
        clearInvocations(eventPublisher);

        routeOptimizer.clear();

        assertThat(solver.isSolving()).isFalse();
        verify(solver).terminateEarly();
        verify(solverFuture).get();

        verify(eventPublisher).publishEvent(routeChangedEventArgumentCaptor.capture());
        RouteChangedEvent event = routeChangedEventArgumentCaptor.getValue();
        assertThat(event.depot()).isEmpty();
        assertThat(event.routes()).isEmpty();
    }

    @Test
    void clear_should_not_fail_when_solver_is_not_solving() {
        assertThatCode(() -> routeOptimizer.clear()).doesNotThrowAnyException();
    }

    @Test
    void reset_interrupted_flag() throws ExecutionException, InterruptedException {
        when(solverFuture.isDone()).thenReturn(true);
        when(solverFuture.get()).thenThrow(InterruptedException.class);
        // start org.optaweb.vehiclerouting.solver by adding 2 locations
        routeOptimizer.addLocation(locationNew1, distanceMatrix);
        routeOptimizer.addLocation(locationNew2, distanceMatrix);

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> routeOptimizer.removeLocation(locationNew2));
        assertThat(Thread.interrupted()).isTrue();

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> routeOptimizer.clear());
        assertThat(Thread.interrupted()).isTrue();
    }

    @Test
    void planning_location_should_have_same_latitude_and_longitude() {
        RoadLocation roadLocation = planningLocation(locationNew1);
        assertThat(roadLocation.getId()).isEqualTo(locationNew1.id());
        assertThat(roadLocation.getLatitude()).isEqualTo(locationNew1.coordinates().latitude().doubleValue());
        assertThat(roadLocation.getLongitude()).isEqualTo(locationNew1.coordinates().longitude().doubleValue());
    }

    /**
     * Create a solution with 1 vehicle with depot being the first location and visiting all customers specified by
     * the rest of locations.
     *
     * @param domainLocationNews depot and customer locations
     * @return initialized solution
     */
    private static VehicleRoutingSolution createSolution(LocationNew... domainLocationNews) {
        VehicleRoutingSolution solution = SolutionUtil.emptySolution();
        Depot depot = SolutionUtil.addDepot(solution, planningLocation(domainLocationNews[0]));
        SolutionUtil.addVehicle(solution, 1);
        SolutionUtil.moveAllVehiclesTo(solution, depot);

        // create customers
        for (int i = 1; i < domainLocationNews.length; i++) {
            SolutionUtil.addCustomer(solution, planningLocation(domainLocationNews[i]));
        }

        // visit all customers
        Standstill previousStandstill = solution.getVehicleList().get(0);
        for (Customer customer : solution.getCustomerList()) {
            customer.setPreviousStandstill(previousStandstill);
            previousStandstill.setNextCustomer(customer);
            previousStandstill = customer;
        }
        return solution;
    }
}
