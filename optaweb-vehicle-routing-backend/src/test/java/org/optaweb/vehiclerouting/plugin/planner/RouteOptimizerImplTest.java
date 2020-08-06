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
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.optaweb.vehiclerouting.domain.VehicleFactory.createVehicle;
import static org.optaweb.vehiclerouting.domain.VehicleFactory.testVehicle;
import static org.optaweb.vehiclerouting.plugin.planner.domain.PlanningLocationFactory.fromDomain;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.Distance;
import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.domain.Vehicle;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningDepot;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVehicle;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVisit;
import org.optaweb.vehiclerouting.plugin.planner.domain.VehicleRoutingSolution;
import org.optaweb.vehiclerouting.service.location.DistanceMatrixRow;

@ExtendWith(MockitoExtension.class)
class RouteOptimizerImplTest {

    private final DistanceMatrixRow matrixRow = locationId -> Distance.ZERO;
    private final Location location1 = new Location(1, Coordinates.valueOf(1.0, 0.1));
    private final Location location2 = new Location(2, Coordinates.valueOf(0.2, 2.2));
    private final Location location3 = new Location(3, Coordinates.valueOf(3.4, 5.6));

    @Captor
    private ArgumentCaptor<VehicleRoutingSolution> solutionArgumentCaptor;
    @Captor
    private ArgumentCaptor<PlanningVehicle> vehicleArgumentCaptor;

    @Mock
    private SolverManager solverManager;
    @Mock
    private RouteChangedEventPublisher routeChangedEventPublisher;
    @InjectMocks
    private RouteOptimizerImpl routeOptimizer;

    @Test
    void solution_with_depot_and_no_visits_should_be_published() {
        // arrange
        Long[] vehicleIds = { 2L, 3L, 5L, 7L, 11L };
        Arrays.stream(vehicleIds).forEach(vehicleId -> routeOptimizer.addVehicle(testVehicle(vehicleId)));
        clearInvocations(routeChangedEventPublisher);

        // act
        routeOptimizer.addLocation(location1, matrixRow);

        // assert
        verifyNoInteractions(solverManager);
        VehicleRoutingSolution solution = verifyPublishingPreliminarySolution();
        assertThat(solution.getVehicleList())
                .extracting(PlanningVehicle::getId)
                .containsExactlyInAnyOrder(vehicleIds);
        assertThat(solution.getDepotList()).extracting(PlanningDepot::getId).containsExactly(location1.id());
        assertThat(solution.getVisitList()).isEmpty();
    }

    @Test
    void solution_with_vehicles_and_no_depot_should_be_published() {
        // arrange
        final long vehicleId = 7;
        final Vehicle vehicle = testVehicle(vehicleId);

        // act 1
        routeOptimizer.addVehicle(vehicle);

        // assert 1
        verifyNoInteractions(solverManager);
        VehicleRoutingSolution solutionWithOneVehicle = verifyPublishingPreliminarySolution();
        assertThat(solutionWithOneVehicle.getVehicleList())
                .extracting(PlanningVehicle::getId)
                .containsExactly(vehicleId);
        assertThat(solutionWithOneVehicle.getDepotList()).isEmpty();
        assertThat(solutionWithOneVehicle.getVisitList()).isEmpty();

        // act 2
        clearInvocations(routeChangedEventPublisher);
        routeOptimizer.removeVehicle(vehicle);

        // assert 2
        verifyNoInteractions(solverManager);
        VehicleRoutingSolution emptySolution = verifyPublishingPreliminarySolution();
        assertThat(emptySolution.getVehicleList()).isEmpty();
        assertThat(emptySolution.getDepotList()).isEmpty();
        assertThat(emptySolution.getVisitList()).isEmpty();
    }

    @Test
    void removing_wrong_vehicle_should_fail_fast() {
        // arrange
        final long vehicleId = 7;
        final Vehicle vehicle = testVehicle(vehicleId);
        final Vehicle nonExistentVehicle = testVehicle(vehicleId + 1);
        routeOptimizer.addVehicle(vehicle);

        // act & assert
        assertThatIllegalArgumentException()
                .isThrownBy(() -> routeOptimizer.removeVehicle(nonExistentVehicle))
                .withMessageContaining("exist");
    }

    @Test
    void removing_wrong_location_should_fail_fast() {
        // no locations
        assertThatIllegalArgumentException()
                .isThrownBy(() -> routeOptimizer.removeLocation(location1))
                .withMessageContaining("no locations");

        // only depot
        routeOptimizer.addLocation(location1, matrixRow);
        assertThatIllegalArgumentException()
                .isThrownBy(() -> routeOptimizer.removeLocation(location3))
                .withMessageContaining("exist");

        // depot and a visit
        routeOptimizer.addLocation(location2, matrixRow);
        assertThatIllegalArgumentException()
                .isThrownBy(() -> routeOptimizer.removeLocation(location3))
                .withMessageContaining("exist");
    }

    @Test
    void added_vehicle_should_be_moved_to_the_depot_even_if_solver_is_not_yet_solving() {
        // arrange
        // -- depot
        routeOptimizer.addLocation(location1, matrixRow);
        // -- vehicles
        routeOptimizer.addVehicle(testVehicle(7));
        routeOptimizer.addVehicle(testVehicle(8));

        // act
        // -- first visit
        routeOptimizer.addLocation(location2, matrixRow);

        // assert
        VehicleRoutingSolution solution = verifySolverStartedWithSolution();
        assertThat(solution.getVehicleList())
                .hasSize(2)
                .allMatch(vehicle -> vehicle.getDepot().getId() == location1.id());

        assertThat(solution.getDepotList()).isNotEmpty();
        assertThat(solution.getVisitList()).isNotEmpty();
    }

    @Test
    void solver_should_start_when_vehicle_is_added_and_there_is_at_least_one_visit() {
        // arrange
        routeOptimizer.addLocation(location1, matrixRow);
        routeOptimizer.addLocation(location2, matrixRow);
        verifyNoInteractions(solverManager);

        // act
        routeOptimizer.addVehicle(testVehicle(9));

        // assert
        VehicleRoutingSolution solution = verifySolverStartedWithSolution();
        assertThat(solution.getVehicleList()).hasSize(1);
        assertThat(solution.getVisitList()).hasSize(1);
    }

    @Test
    void each_location_should_have_a_distance_map_after_it_is_added() {
        long millis = 8079;
        routeOptimizer.addLocation(location1, locationId -> Distance.ofMillis(millis));

        VehicleRoutingSolution solution = verifyPublishingPreliminarySolution();
        assertThat(solution.getDepotList()).hasSize(1);
        assertThat(solution.getDepotList().get(0).getLocation().distanceTo(fromDomain(location2))).isEqualTo(millis);
    }

    @Test
    void solver_should_start_when_two_locations_added_and_there_is_at_least_one_vehicle() {
        // add 1 vehicle, 2 locations
        routeOptimizer.addVehicle(testVehicle(1));
        routeOptimizer.addLocation(location1, matrixRow);
        routeOptimizer.addLocation(location2, matrixRow);

        // solving has started after adding a second location (=> depot + visit)
        VehicleRoutingSolution solution = verifySolverStartedWithSolution();

        assertThat(solution.getDepotList()).hasSize(1);
        assertThat(solution.getDepotList().get(0).getLocation().getId()).isEqualTo(location1.id());
        assertThat(solution.getVisitList()).hasSize(1);
        assertThat(solution.getVisitList().get(0).getLocation().getId()).isEqualTo(location2.id());
    }

    @Test
    void solver_should_not_start_nor_stop_when_modifying_location_and_there_are_no_vehicles() {
        // add 2 locations
        routeOptimizer.addLocation(location1, matrixRow);
        clearInvocations(routeChangedEventPublisher);
        routeOptimizer.addLocation(location2, matrixRow);

        // solving did not start due to missing vehicles
        verify(solverManager, never()).startSolver(any());
        // but preliminary solution is published
        VehicleRoutingSolution solution1 = verifyPublishingPreliminarySolution();
        assertThat(solution1.getVehicleList()).isEmpty();
        assertThat(solution1.getDepotList()).hasSize(1);
        assertThat(solution1.getVisitList()).hasSize(1);

        // add a third location and remove another one
        routeOptimizer.addLocation(location3, matrixRow);
        clearInvocations(routeChangedEventPublisher);
        routeOptimizer.removeLocation(location2);

        // no interactions with solver (start/stop/problem fact changes) because
        // it hasn't started (due to missing vehicles)
        verifyNoInteractions(solverManager);
        // but preliminary solution is published
        VehicleRoutingSolution solution2 = verifyPublishingPreliminarySolution();
        assertThat(solution2.getVehicleList()).isEmpty();
        assertThat(solution1.getDepotList()).hasSize(1);
        assertThat(solution1.getVisitList()).hasSize(1);
    }

    @Test
    void solver_should_stop_and_publish_when_last_vehicle_is_removed() {
        Vehicle vehicle = testVehicle(23);
        routeOptimizer.addVehicle(vehicle);
        routeOptimizer.addLocation(location1, matrixRow);
        routeOptimizer.addLocation(location2, matrixRow);
        verify(solverManager).startSolver(any(VehicleRoutingSolution.class));
        clearInvocations(routeChangedEventPublisher);

        routeOptimizer.removeVehicle(vehicle);
        verify(solverManager).stopSolver();
        VehicleRoutingSolution solution = verifyPublishingPreliminarySolution();
        assertThat(solution.getVehicleList()).isEmpty();
    }

    @Test
    void solver_should_stop_when_locations_reduced_to_one() {
        // add 1 vehicle, 2 locations
        routeOptimizer.addVehicle(testVehicle(0));
        routeOptimizer.addLocation(location1, matrixRow);
        routeOptimizer.addLocation(location2, matrixRow);
        verify(solverManager).startSolver(any(VehicleRoutingSolution.class));
        clearInvocations(routeChangedEventPublisher);

        // remove 1 location from running solver
        routeOptimizer.removeLocation(location2);

        verify(solverManager).stopSolver();

        VehicleRoutingSolution solution = verifyPublishingPreliminarySolution();
        assertThat(solution.getVisitList()).isEmpty();
        assertThat(solution.getDepotList()).hasSize(1);
        assertThat(solution.getVehicleList()).hasSize(1);
    }

    @Test
    void removing_depot_impossible_when_there_are_other_locations() {
        routeOptimizer.addVehicle(testVehicle(0));
        // add 2 locations
        routeOptimizer.addLocation(location1, matrixRow);
        routeOptimizer.addLocation(location2, matrixRow);
        verify(solverManager).startSolver(any(VehicleRoutingSolution.class));

        assertThatIllegalStateException()
                .isThrownBy(() -> routeOptimizer.removeLocation(location1))
                .withMessageContaining("depot");
    }

    @Test
    void when_depot_is_added_all_vehicles_should_be_moved_to_it() {
        // given 2 vehicles
        long vehicleId1 = 8;
        long vehicleId2 = 113;
        routeOptimizer.addVehicle(testVehicle(vehicleId1));
        routeOptimizer.addVehicle(testVehicle(vehicleId2));
        clearInvocations(routeChangedEventPublisher);

        // when a depot is added
        routeOptimizer.addLocation(location1, matrixRow);

        // then all vehicles must be in the depot
        VehicleRoutingSolution solution1 = verifyPublishingPreliminarySolution();
        assertThat(solution1.getVehicleList())
                .extracting(PlanningVehicle::getId)
                .containsExactlyInAnyOrder(vehicleId1, vehicleId2);
        assertThat(solution1.getVehicleList()).allMatch(vehicle -> vehicle.getDepot().getId() == location1.id());
        assertThat(solution1.getDepotList()).extracting(PlanningDepot::getId).containsExactly(location1.id());

        // if we remove the depot
        clearInvocations(routeChangedEventPublisher);
        routeOptimizer.removeLocation(location1);

        // then published solution's depot list is empty
        VehicleRoutingSolution solution2 = verifyPublishingPreliminarySolution();
        assertThat(solution2.getVehicleList())
                .extracting(PlanningVehicle::getId)
                .containsExactlyInAnyOrder(vehicleId1, vehicleId2);
        assertThat(solution2.getDepotList()).isEmpty();

        // and it's possible to add a new depot
        routeOptimizer.addLocation(location2, matrixRow);
    }

    @Test
    void adding_location_to_running_solver_must_happen_through_problem_fact_change() {
        // arrange
        routeOptimizer.addVehicle(testVehicle(55));
        routeOptimizer.addLocation(location1, matrixRow);
        routeOptimizer.addLocation(location2, matrixRow);
        verify(solverManager).startSolver(any(VehicleRoutingSolution.class));
        // act
        routeOptimizer.addLocation(location3, matrixRow);
        // assert
        verify(solverManager).addVisit(any(PlanningVisit.class));
    }

    @Test
    void removing_location_from_solver_with_more_than_two_locations_must_happen_through_problem_fact_change() {
        // arrange: set up a situation where solver is running with 1 depot and 2 visits
        long vehicleId = 0;
        routeOptimizer.addVehicle(testVehicle(vehicleId));
        routeOptimizer.addLocation(location1, matrixRow);
        routeOptimizer.addLocation(location2, matrixRow);
        verify(solverManager).startSolver(any(VehicleRoutingSolution.class));

        // add second visit to avoid stopping solver manager after removing a visit below
        routeOptimizer.addLocation(location3, matrixRow);
        verify(solverManager).addVisit(any(PlanningVisit.class));

        // act
        routeOptimizer.removeLocation(location2);

        // assert
        ArgumentCaptor<PlanningVisit> visitArgumentCaptor = ArgumentCaptor.forClass(PlanningVisit.class);
        verify(solverManager).removeVisit(visitArgumentCaptor.capture());
        assertThat(visitArgumentCaptor.getValue().getId()).isEqualTo(location2.id());
        // solver still running
        verify(solverManager, never()).stopSolver();
    }

    @Test
    void adding_vehicle_to_running_solver_must_happen_through_problem_fact_change() {
        // arrange
        routeOptimizer.addVehicle(testVehicle(1));
        routeOptimizer.addLocation(location1, matrixRow);
        routeOptimizer.addLocation(location2, matrixRow);
        verify(solverManager).startSolver(any(VehicleRoutingSolution.class));

        // act
        routeOptimizer.addVehicle(testVehicle(22));

        // assert
        verify(solverManager).addVehicle(vehicleArgumentCaptor.capture());
        PlanningVehicle vehicle = vehicleArgumentCaptor.getValue();
        assertThat(vehicle.getDepot().getId()).isEqualTo(location1.id());
    }

    @Test
    void removing_vehicle_from_running_solver_with_more_than_one_vehicle_must_happen_through_problem_fact_change() {
        // arrange: set up a situation where solver is running with 2 vehicles
        final long vehicleId1 = 10;
        final long vehicleId2 = 20;
        routeOptimizer.addVehicle(testVehicle(vehicleId1));
        routeOptimizer.addVehicle(testVehicle(vehicleId2));
        routeOptimizer.addLocation(location1, matrixRow);
        routeOptimizer.addLocation(location2, matrixRow);
        verify(solverManager).startSolver(any(VehicleRoutingSolution.class));

        // act
        routeOptimizer.removeVehicle(testVehicle(vehicleId1));

        // assert
        verify(solverManager).removeVehicle(any(PlanningVehicle.class));
        // solver still running
        verify(solverManager, never()).stopSolver();
    }

    @Test
    void changing_vehicle_capacity_should_take_effect_when_solver_is_started_or_be_published() {
        // 1 depot, 1 vehicle
        final long vehicleId = 1;
        final int oldCapacity = 7;
        final int newCapacity = 12;
        Vehicle vehicle = createVehicle(vehicleId, "", oldCapacity);
        routeOptimizer.addVehicle(vehicle);
        routeOptimizer.addLocation(location1, matrixRow);
        clearInvocations(routeChangedEventPublisher);

        // change capacity when solver is not running
        routeOptimizer.changeCapacity(createVehicle(vehicleId, "", newCapacity));
        verifyNoInteractions(solverManager);
        VehicleRoutingSolution preliminarySolution = verifyPublishingPreliminarySolution();
        assertThat(preliminarySolution.getVehicleList().get(0).getCapacity()).isEqualTo(newCapacity);

        // start solver
        routeOptimizer.addLocation(location2, matrixRow);

        VehicleRoutingSolution solution = verifySolverStartedWithSolution();
        assertThat(solution.getVehicleList().get(0).getCapacity()).isEqualTo(newCapacity);
    }

    @Test
    void changing_vehicle_capacity_must_happen_through_problem_fact_change_when_solver_is_running() {
        // 1 vehicle, 1 depot, 1 visit
        final int capacity = 14816;
        final long vehicleId = 10;
        routeOptimizer.addVehicle(testVehicle(vehicleId));
        routeOptimizer.addLocation(location1, matrixRow);
        routeOptimizer.addLocation(location2, matrixRow);
        verify(solverManager).startSolver(any(VehicleRoutingSolution.class));

        routeOptimizer.changeCapacity(createVehicle(vehicleId, "", capacity));

        verify(solverManager).changeCapacity(any(PlanningVehicle.class));
    }

    @Test
    void changing_vehicle_capacity_must_fail_fast_if_the_vehicle_does_not_exist() {
        // 1 vehicle, 1 depot, 1 visit
        final long vehicleId = 10;
        routeOptimizer.addVehicle(testVehicle(vehicleId));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> routeOptimizer.changeCapacity(testVehicle(vehicleId + 1)))
                .withMessageContaining("exist");
    }

    @Test
    void remove_all_locations_should_stop_solver_and_publish_preliminary_solution() {
        // set up a situation where solver is running with 1 depot and 2 visits
        long vehicleId = 10;
        routeOptimizer.addVehicle(testVehicle(vehicleId));
        routeOptimizer.addLocation(location1, matrixRow);
        routeOptimizer.addLocation(location2, matrixRow);
        verify(solverManager).startSolver(any(VehicleRoutingSolution.class));
        routeOptimizer.addLocation(location3, matrixRow);
        clearInvocations(routeChangedEventPublisher);

        routeOptimizer.removeAllLocations();

        verify(solverManager).stopSolver();

        VehicleRoutingSolution solution = verifyPublishingPreliminarySolution();
        assertThat(solution.getVehicleList()).hasSize(1);
        assertThat(solution.getDepotList()).isEmpty();
        assertThat(solution.getVisitList()).isEmpty();
    }

    @Test
    void remove_all_vehicles_should_stop_solver_and_publish_preliminary_solution() {
        long vehicleId = 10;
        routeOptimizer.addVehicle(testVehicle(vehicleId));
        routeOptimizer.addLocation(location1, matrixRow);
        routeOptimizer.addLocation(location2, matrixRow);
        verify(solverManager).startSolver(any(VehicleRoutingSolution.class));
        routeOptimizer.addLocation(location3, matrixRow);
        clearInvocations(routeChangedEventPublisher);

        routeOptimizer.removeAllVehicles();

        verify(solverManager).stopSolver();

        VehicleRoutingSolution solution = verifyPublishingPreliminarySolution();
        assertThat(solution.getVehicleList()).isEmpty();
        assertThat(solution.getDepotList()).hasSize(1);
        assertThat(solution.getVisitList()).hasSize(2);
    }

    @Test
    void removing_all_locations_should_not_fail_when_solver_is_not_solving() {
        assertThatCode(() -> routeOptimizer.removeAllLocations()).doesNotThrowAnyException();
    }

    @Test
    void removing_all_vehicles_should_not_fail_when_solver_is_not_solving() {
        assertThatCode(() -> routeOptimizer.removeAllVehicles()).doesNotThrowAnyException();
    }

    private VehicleRoutingSolution verifyPublishingPreliminarySolution() {
        verify(routeChangedEventPublisher).publishSolution(solutionArgumentCaptor.capture());
        return solutionArgumentCaptor.getValue();
    }

    private VehicleRoutingSolution verifySolverStartedWithSolution() {
        verify(solverManager).startSolver(solutionArgumentCaptor.capture());
        return solutionArgumentCaptor.getValue();
    }
}
