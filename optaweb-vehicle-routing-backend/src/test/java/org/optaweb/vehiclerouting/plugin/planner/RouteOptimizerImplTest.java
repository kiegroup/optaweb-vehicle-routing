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
import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.Depot;
import org.optaplanner.examples.vehiclerouting.domain.Standstill;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.domain.location.RoadLocation;
import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.domain.Vehicle;
import org.optaweb.vehiclerouting.plugin.planner.change.AddCustomer;
import org.optaweb.vehiclerouting.plugin.planner.change.AddVehicle;
import org.optaweb.vehiclerouting.plugin.planner.change.RemoveVehicle;
import org.optaweb.vehiclerouting.service.location.DistanceMatrix;
import org.optaweb.vehiclerouting.service.route.RouteChangedEvent;
import org.optaweb.vehiclerouting.service.route.ShallowRoute;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.task.AsyncTaskExecutor;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
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

    private final Location location1 = new Location(1, Coordinates.valueOf(1.0, 0.1));
    private final Location location2 = new Location(2, Coordinates.valueOf(0.2, 2.2));
    private final Location location3 = new Location(3, Coordinates.valueOf(3.4, 5.6));

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
        // we can then verify that solver.solve() has been called
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
        long vehicleId = 33;
        VehicleRoutingSolution solution = createSolutionWithOneVehicle(vehicleId, location1, location2);
        when(bestSolutionChangedEvent.isEveryProblemFactChangeProcessed()).thenReturn(true);
        when(bestSolutionChangedEvent.getNewBestSolution()).thenReturn(solution);

        routeOptimizer.bestSolutionChanged(bestSolutionChangedEvent);

        RouteChangedEvent event = verifyAndCaptureEvent();
        assertThat(event.vehicleIds()).containsExactly(vehicleId);
        assertThat(event.depotId()).contains(location1.id());
        assertThat(event.routes()).hasSize(1);
        for (ShallowRoute route : event.routes()) {
            assertThat(route.depotId).isEqualTo(location1.id());
            assertThat(route.visitIds).containsExactly(location2.id());
            assertThat(route.vehicleId).isEqualTo(vehicleId);
        }
    }

    @Test
    void solution_with_depot_and_no_visits_should_be_published() {
        // arrange
        Long[] vehicleIds = {2L, 3L, 5L, 7L, 11L};
        Arrays.stream(vehicleIds).forEach(vehicleId -> routeOptimizer.addVehicle(vehicle(vehicleId)));
        clearInvocations(eventPublisher);

        // act
        routeOptimizer.addLocation(location1, distanceMatrix);

        // assert
        RouteChangedEvent event = verifyAndCaptureEvent();
        assertThat(solver.isSolving()).isFalse();
        assertThat(event.vehicleIds()).containsExactlyInAnyOrder(vehicleIds);
        assertThat(event.depotId()).contains(location1.id());
        assertThat(event.routes()).hasSameSizeAs(vehicleIds);
        assertThat(event.routes().stream().mapToLong(value -> value.vehicleId)).containsExactlyInAnyOrder(vehicleIds);
        for (ShallowRoute route : event.routes()) {
            assertThat(route.depotId).isEqualTo(location1.id());
            assertThat(route.visitIds).isEmpty();
        }
    }

    @Test
    void solution_with_vehicles_and_no_depot_should_be_published() {
        // arrange
        final long vehicleId = 7;
        final Vehicle vehicle = vehicle(vehicleId);

        // act 1
        routeOptimizer.addVehicle(vehicle);

        // assert 1
        assertThat(solver.isSolving()).isFalse();
        RouteChangedEvent event1 = verifyAndCaptureEvent();
        assertThat(event1.vehicleIds()).containsExactly(vehicleId);
        assertThat(event1.depotId()).isEmpty();
        assertThat(event1.routes()).isEmpty();

        // act 2
        routeOptimizer.removeVehicle(vehicle);

        // assert 2
        assertThat(solver.isSolving()).isFalse();
        RouteChangedEvent event2 = verifyAndCaptureEvent();
        assertThat(event2.vehicleIds()).isEmpty();
        assertThat(event2.depotId()).isEmpty();
        assertThat(event2.routes()).isEmpty();
    }

    @Test
    void removing_wrong_vehicle_should_fail_fast() {
        // arrange
        final long vehicleId = 7;
        final Vehicle vehicle = vehicle(vehicleId);
        final Vehicle nonExistentVehicle = vehicle(vehicleId + 1);
        routeOptimizer.addVehicle(vehicle);

        // act & assert
        assertThatIllegalArgumentException()
                .isThrownBy(() -> routeOptimizer.removeVehicle(nonExistentVehicle))
                .withMessageContaining("vehicle");
    }

    @Test
    void added_vehicle_should_be_moved_to_the_depot_even_if_solver_is_not_yet_solving() {
        // arrange
        final long vehicleId = 10;
        routeOptimizer.addLocation(location1, distanceMatrix);
        clearInvocations(eventPublisher);

        // act
        routeOptimizer.addVehicle(vehicle(vehicleId));

        // assert
        assertThat(solver.isSolving()).isFalse(); // with a depot and no visits, solver is not yet solving
        // Can't exactly verify that vehicle.depot == depot but without that, even publishing would fail.
        RouteChangedEvent event = verifyAndCaptureEvent();
        assertThat(event.vehicleIds()).containsExactly(vehicleId);
        assertThat(event.depotId()).isNotEmpty();
        assertThat(event.routes()).hasSize(1);
    }

    @Test
    void solver_should_start_when_two_locations_added() {
        // add 2 locations
        routeOptimizer.addLocation(location1, distanceMatrix);
        routeOptimizer.addLocation(location2, distanceMatrix);

        assertThat(isSolving).isTrue();
        assertThat(solver.isSolving()).isTrue();
        // solving has started after adding a second location (depot + visit)
        verify(solver).solve(any());

        // problem fact change only happens when adding location to a running solver
        // or when more than 1 location remains after removing one
        verify(solver, never()).addProblemFactChange(any());
    }

    @Test
    void solver_should_stop_when_locations_reduced_to_one() throws ExecutionException, InterruptedException {
        // add 2 locations
        routeOptimizer.addLocation(location1, distanceMatrix);
        routeOptimizer.addLocation(location2, distanceMatrix);

        // remove 1 location from running solver
        assertThat(solver.isSolving()).isTrue();
        routeOptimizer.removeLocation(location2);

        assertThat(solver.isSolving()).isFalse();
        verify(solver).terminateEarly();
        verify(solverFuture).get();

        // problem fact change only happens when adding location to a running solver
        // or when more than 1 location remains after removing one
        verify(solver, never()).addProblemFactChange(any());
    }

    @Test
    void solution_update_event_should_only_have_empty_routes_when_last_visit_removed() {
        // FIXME This test shouldn't be needed. This is a problem with bad encapsulation of the planning domain in
        //   optaplanner-examples. Once we introduce our own planning domain with a better API, the test should be
        //   replaced/simplified/removed.

        // Prepare a solution with 1 depot, 2 vehicles, 1 customer and both vehicles visiting to that customer
        final VehicleRoutingSolution solution = SolutionUtil.emptySolution();
        final long vehicleId1 = 1;
        final long vehicleId2 = 2;
        SolutionUtil.addVehicle(solution, vehicleId1);
        SolutionUtil.addVehicle(solution, vehicleId2);
        final Depot depot = SolutionUtil.addDepot(solution, planningLocation(location1));
        SolutionUtil.moveAllVehiclesTo(solution, depot);
        final Customer customer = SolutionUtil.addCustomer(solution, planningLocation(location2));
        solution.getVehicleList().forEach(vehicle -> vehicle.setNextCustomer(customer));
        assertThat(SolutionUtil.routes(solution)).allMatch(shallowRoute -> shallowRoute.visitIds.size() == 1);
        solution.setScore(HardSoftLongScore.ofSoft(-1000)); // set non-zero travel distance

        // Start solver by adding two locations
        routeOptimizer.addLocation(location1, distanceMatrix);
        routeOptimizer.addLocation(location2, distanceMatrix);

        // Pretend solver found a new best best solution
        when(bestSolutionChangedEvent.isEveryProblemFactChangeProcessed()).thenReturn(true);
        when(bestSolutionChangedEvent.getNewBestSolution()).thenReturn(solution);
        routeOptimizer.bestSolutionChanged(bestSolutionChangedEvent);

        clearInvocations(eventPublisher);

        routeOptimizer.removeLocation(location2);
        assertThat(routeOptimizer.isSolving()).isFalse();

        // no customer -> all routes should be empty
        RouteChangedEvent event = verifyAndCaptureEvent();
        assertThat(event.distance()).isEqualTo("0h 0m 0s"); // expect zero travel distance
        assertThat(event.vehicleIds()).containsExactlyInAnyOrder(vehicleId1, vehicleId2);
        assertThat(event.depotId()).isPresent();
        assertThat(event.routes()).hasSameSizeAs(solution.getVehicleList());
        for (ShallowRoute route : event.routes()) {
            assertThat(route.visitIds).isEmpty();
        }
    }

    @Test
    void removing_depot_impossible_when_there_are_other_locations() {
        // add 2 locations
        routeOptimizer.addLocation(location1, distanceMatrix);
        routeOptimizer.addLocation(location2, distanceMatrix);

        assertThatIllegalStateException()
                .isThrownBy(() -> routeOptimizer.removeLocation(location1))
                .withMessageContaining("depot");
    }

    @Test
    void when_depot_is_added_all_vehicles_should_be_moved_to_it() {
        // given 2 vehicles
        long vehicleId1 = 8;
        long vehicleId2 = 113;
        routeOptimizer.addVehicle(vehicle(vehicleId1));
        routeOptimizer.addVehicle(vehicle(vehicleId2));
        clearInvocations(eventPublisher);

        // when a depot is added
        routeOptimizer.addLocation(location1, distanceMatrix);

        // then all vehicles must be in the depot
        RouteChangedEvent event1 = verifyAndCaptureEvent();
        assertThat(event1.vehicleIds()).containsExactlyInAnyOrder(vehicleId1, vehicleId2);
        // NOTE: can't verify that vehicle.getDepot() == depot for each vehicle because that's internal
        // to the optimizer. Neither VehicleRoutingSolution nor any other planning domain classes are exposed
        // to the outside. But the fact that RouteChangedEvent is published successfully proves that vehicles
        // have been moved to the depot.
        assertThat(event1.depotId()).contains(location1.id());
        assertThat(event1.routes()).hasSize(2);
        assertThat(event1.routes().stream().mapToLong(value -> value.vehicleId))
                .containsExactlyInAnyOrder(vehicleId1, vehicleId2);

        // if whe remove the depot
        clearInvocations(eventPublisher);
        routeOptimizer.removeLocation(location1);

        // then published event's depot value is empty
        RouteChangedEvent event2 = verifyAndCaptureEvent();
        assertThat(event2.vehicleIds()).containsExactlyInAnyOrder(vehicleId1, vehicleId2);
        assertThat(event2.depotId()).isEmpty();
        assertThat(event2.routes()).isEmpty();

        // and it's possible to add a new depot
        routeOptimizer.addLocation(location2, distanceMatrix);
    }

    @Test
    void adding_location_to_running_solver_must_happen_through_problem_fact_change() {
        // arrange
        routeOptimizer.addLocation(location1, distanceMatrix);
        assertThat(solver.isSolving()).isFalse();
        routeOptimizer.addLocation(location2, distanceMatrix);
        assertThat(solver.isSolving()).isTrue();
        // act
        routeOptimizer.addLocation(location3, distanceMatrix);
        // assert
        verify(solver).addProblemFactChange(any(AddCustomer.class));
    }

    @Test
    void removing_location_from_solver_with_more_than_two_locations_must_happen_through_problem_fact_change() {
        // set up a situation where solver is running with 1 depot and 2 visits
        long vehicleId = 0;
        VehicleRoutingSolution solution = createSolutionWithOneVehicle(vehicleId, location1, location2, location3);
        when(bestSolutionChangedEvent.isEveryProblemFactChangeProcessed()).thenReturn(true);
        when(bestSolutionChangedEvent.getNewBestSolution()).thenReturn(solution);
        routeOptimizer.addLocation(location1, distanceMatrix);
        routeOptimizer.addLocation(location2, distanceMatrix);
        routeOptimizer.addLocation(location3, distanceMatrix);
        routeOptimizer.bestSolutionChanged(bestSolutionChangedEvent);

        routeOptimizer.removeLocation(location2);
        verify(solver).addProblemFactChanges(any()); // note that it's plural
        // solver still running
        verify(solver, never()).terminateEarly();
    }

    @Test
    void adding_vehicle_to_running_solver_must_happen_through_problem_fact_change() {
        // arrange
        routeOptimizer.addLocation(location1, distanceMatrix);
        assertThat(solver.isSolving()).isFalse();
        routeOptimizer.addLocation(location2, distanceMatrix);
        assertThat(solver.isSolving()).isTrue();
        // act
        routeOptimizer.addVehicle(vehicle(22));
        // assert
        verify(solver).addProblemFactChange(any(AddVehicle.class));
    }

    @Test
    void removing_vehicle_from_running_solver_with_more_than_one_vehicle_must_happen_through_problem_fact_change() {
        // set up a situation where solver is running with 2 vehicles
        final long vehicleId1 = 10;
        final long vehicleId2 = 20;
        VehicleRoutingSolution solution = createSolution(
                asList(vehicleId1, vehicleId2),
                location1, location2, location3
        );
        when(bestSolutionChangedEvent.isEveryProblemFactChangeProcessed()).thenReturn(true);
        when(bestSolutionChangedEvent.getNewBestSolution()).thenReturn(solution);
        routeOptimizer.addLocation(location1, distanceMatrix);
        routeOptimizer.addLocation(location2, distanceMatrix);
        routeOptimizer.addLocation(location3, distanceMatrix);
        routeOptimizer.bestSolutionChanged(bestSolutionChangedEvent);

        routeOptimizer.removeVehicle(vehicle(vehicleId1));
        verify(solver).addProblemFactChange(any(RemoveVehicle.class));
        // solver still running
        verify(solver, never()).terminateEarly();
    }

    @Test
    void clear_should_stop_solver_and_publish_initial_solution() throws ExecutionException, InterruptedException {
        // set up a situation where solver is running with 1 depot and 2 visits
        long vehicleId = 10;
        VehicleRoutingSolution solution = createSolutionWithOneVehicle(vehicleId, location1, location2, location3);
        when(bestSolutionChangedEvent.isEveryProblemFactChangeProcessed()).thenReturn(true);
        when(bestSolutionChangedEvent.getNewBestSolution()).thenReturn(solution);
        routeOptimizer.addLocation(location1, distanceMatrix);
        routeOptimizer.addLocation(location2, distanceMatrix);
        routeOptimizer.addLocation(location3, distanceMatrix);
        routeOptimizer.bestSolutionChanged(bestSolutionChangedEvent);
        clearInvocations(eventPublisher);

        routeOptimizer.clear();

        assertThat(solver.isSolving()).isFalse();
        verify(solver).terminateEarly();
        verify(solverFuture).get();

        RouteChangedEvent event = verifyAndCaptureEvent();
        assertThat(event.vehicleIds()).isEmpty();
        assertThat(event.depotId()).isEmpty();
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
        // start solver by adding 2 locations
        routeOptimizer.addLocation(location1, distanceMatrix);
        routeOptimizer.addLocation(location2, distanceMatrix);

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> routeOptimizer.removeLocation(location2));
        assertThat(Thread.interrupted()).isTrue();

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> routeOptimizer.clear());
        assertThat(Thread.interrupted()).isTrue();
    }

    @Test
    void planning_location_should_have_same_latitude_and_longitude() {
        RoadLocation roadLocation = planningLocation(location1);
        assertThat(roadLocation.getId()).isEqualTo(location1.id());
        assertThat(roadLocation.getLatitude()).isEqualTo(location1.coordinates().latitude().doubleValue());
        assertThat(roadLocation.getLongitude()).isEqualTo(location1.coordinates().longitude().doubleValue());
    }

    private RouteChangedEvent verifyAndCaptureEvent() {
        verify(eventPublisher).publishEvent(routeChangedEventArgumentCaptor.capture());
        clearInvocations(eventPublisher);
        return routeChangedEventArgumentCaptor.getValue();
    }

    /**
     * Create an initialized solution with a single vehicle, a depot being the first location,
     * and optional number of customers, all visited by the vehicle
     * @param vehicleId vehicle ID
     * @param domainLocations depot and customer locations
     * @return initialized solution
     */
    private static VehicleRoutingSolution createSolutionWithOneVehicle(long vehicleId, Location... domainLocations) {
        return createSolution(singletonList(vehicleId), domainLocations);
    }

    /**
     * Create an initialized solution with several vehicles, a depot being the first location,
     * and optional number of customers, all visited by the <strong>first vehicle</strong>.
     * Other vehicles are left idle.
     * @param vehicleIds vehicle IDs
     * @param domainLocations depot and customer locations
     * @return initialized solution
     */
    private static VehicleRoutingSolution createSolution(List<Long> vehicleIds, Location... domainLocations) {
        VehicleRoutingSolution solution = SolutionUtil.emptySolution();
        Depot depot = SolutionUtil.addDepot(solution, planningLocation(domainLocations[0]));
        vehicleIds.forEach(vehicleId -> SolutionUtil.addVehicle(solution, vehicleId));
        SolutionUtil.moveAllVehiclesTo(solution, depot);

        // create customers
        for (int i = 1; i < domainLocations.length; i++) {
            SolutionUtil.addCustomer(solution, planningLocation(domainLocations[i]));
        }

        if (vehicleIds.isEmpty()) {
            return solution;
        }

        // visit all customers with the first vehicle
        Standstill previousStandstill = solution.getVehicleList().get(0);
        for (Customer customer : solution.getCustomerList()) {
            customer.setPreviousStandstill(previousStandstill);
            previousStandstill.setNextCustomer(customer);
            previousStandstill = customer;
        }
        return solution;
    }

    private static Vehicle vehicle(long id) {
        return new Vehicle(id, "");
    }
}
