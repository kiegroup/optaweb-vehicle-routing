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
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.vehiclerouting.domain.Depot;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.domain.location.RoadLocation;
import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.domain.Vehicle;
import org.optaweb.vehiclerouting.service.location.DistanceMatrix;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class RouteOptimizerImplTest {

    private final Location location1 = new Location(1, Coordinates.valueOf(1.0, 0.1));
    private final Location location2 = new Location(2, Coordinates.valueOf(0.2, 2.2));
    private final Location location3 = new Location(3, Coordinates.valueOf(3.4, 5.6));

    @Captor
    private ArgumentCaptor<VehicleRoutingSolution> solutionArgumentCaptor;
    @Captor
    private ArgumentCaptor<org.optaplanner.examples.vehiclerouting.domain.Vehicle> vehicleArgumentCaptor;
    @Mock
    private DistanceMatrix distanceMatrix;

    @Mock
    private SolverManager solverManager;
    @Mock
    private SolutionPublisher solutionPublisher;
    @InjectMocks
    private RouteOptimizerImpl routeOptimizer;

    @Test
    void solution_with_depot_and_no_visits_should_be_published() {
        // arrange
        Long[] vehicleIds = {2L, 3L, 5L, 7L, 11L};
        Arrays.stream(vehicleIds).forEach(vehicleId -> routeOptimizer.addVehicle(vehicle(vehicleId)));
        clearInvocations(solutionPublisher);

        // act
        routeOptimizer.addLocation(location1, distanceMatrix);

        // assert
        verifyZeroInteractions(solverManager);
        VehicleRoutingSolution solution = verifyPublishingPreliminarySolution();
        assertThat(solution.getVehicleList())
                .extracting(AbstractPersistable::getId)
                .containsExactlyInAnyOrder(vehicleIds);
        assertThat(solution.getDepotList()).extracting(Depot::getId).containsExactly(location1.id());
        assertThat(solution.getCustomerList()).isEmpty();
    }

    @Test
    void solution_with_vehicles_and_no_depot_should_be_published() {
        // arrange
        final long vehicleId = 7;
        final Vehicle vehicle = vehicle(vehicleId);

        // act 1
        routeOptimizer.addVehicle(vehicle);

        // assert 1
        verifyZeroInteractions(solverManager);
        VehicleRoutingSolution solutionWithOneVehicle = verifyPublishingPreliminarySolution();
        assertThat(solutionWithOneVehicle.getVehicleList())
                .extracting(AbstractPersistable::getId)
                .containsExactly(vehicleId);
        assertThat(solutionWithOneVehicle.getDepotList()).isEmpty();
        assertThat(solutionWithOneVehicle.getCustomerList()).isEmpty();

        // act 2
        clearInvocations(solutionPublisher);
        routeOptimizer.removeVehicle(vehicle);

        // assert 2
        verifyZeroInteractions(solverManager);
        VehicleRoutingSolution emptySolution = verifyPublishingPreliminarySolution();
        assertThat(emptySolution.getVehicleList()).isEmpty();
        assertThat(emptySolution.getDepotList()).isEmpty();
        assertThat(emptySolution.getCustomerList()).isEmpty();
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
                .withMessageContaining("exist");
    }

    @Test
    void removing_wrong_location_should_fail_fast() {
        // no locations
        assertThatIllegalArgumentException()
                .isThrownBy(() -> routeOptimizer.removeLocation(location1))
                .withMessageContaining("no locations");

        // only depot
        routeOptimizer.addLocation(location1, distanceMatrix);
        assertThatIllegalArgumentException()
                .isThrownBy(() -> routeOptimizer.removeLocation(location3))
                .withMessageContaining("exist");

        // depot and a visit
        routeOptimizer.addLocation(location2, distanceMatrix);
        assertThatIllegalArgumentException()
                .isThrownBy(() -> routeOptimizer.removeLocation(location3))
                .withMessageContaining("exist");
    }

    @Test
    void added_vehicle_should_be_moved_to_the_depot_even_if_solver_is_not_yet_solving() {
        // arrange
        // -- depot
        routeOptimizer.addLocation(location1, distanceMatrix);
        // -- vehicles
        routeOptimizer.addVehicle(vehicle(7));
        routeOptimizer.addVehicle(vehicle(8));

        // act
        // -- first visit
        routeOptimizer.addLocation(location2, distanceMatrix);

        // assert
        VehicleRoutingSolution solution = verifySolverStartedWithSolution();
        assertThat(solution.getVehicleList())
                .hasSize(2)
                .allMatch(vehicle -> vehicle.getDepot().getId().equals(location1.id()));

        assertThat(solution.getDepotList()).isNotEmpty();
        assertThat(solution.getCustomerList()).isNotEmpty();
    }

    @Test
    void solver_should_start_when_vehicle_is_added_and_there_is_at_least_one_visit() {
        // arrange
        routeOptimizer.addLocation(location1, distanceMatrix);
        routeOptimizer.addLocation(location2, distanceMatrix);
        verifyZeroInteractions(solverManager);

        // act
        routeOptimizer.addVehicle(vehicle(9));

        // assert
        VehicleRoutingSolution solution = verifySolverStartedWithSolution();
        assertThat(solution.getVehicleList()).hasSize(1);
        assertThat(solution.getCustomerList()).hasSize(1);
    }

    @Test
    void each_location_should_have_a_distance_map_after_it_is_added() {
        Map<Long, Double> distanceMap = new HashMap<>(1);
        double distance = 8.079;
        distanceMap.put(location2.id(), distance);
        when(distanceMatrix.getRow(location1)).thenReturn(distanceMap);
        routeOptimizer.addLocation(location1, distanceMatrix);

        verify(distanceMatrix).getRow(location1);
        VehicleRoutingSolution solution = verifyPublishingPreliminarySolution();
        assertThat(solution.getDepotList()).hasSize(1);
        assertThat(solution.getDepotList().get(0).getLocation().getDistanceTo(LocationFactory.fromDomain(location2)))
                .isEqualTo(8);
        //FIXME should be: .isEqualTo(distance);
    }

    @Test
    void solver_should_start_when_two_locations_added_and_there_is_at_least_one_vehicle() {
        // add 1 vehicle, 2 locations
        routeOptimizer.addVehicle(vehicle(1));
        routeOptimizer.addLocation(location1, distanceMatrix);
        routeOptimizer.addLocation(location2, distanceMatrix);

        // solving has started after adding a second location (=> depot + visit)
        VehicleRoutingSolution solution = verifySolverStartedWithSolution();

        assertThat(solution.getDepotList()).hasSize(1);
        assertThat(solution.getDepotList().get(0).getLocation().getId()).isEqualTo(location1.id());
        assertThat(solution.getCustomerList()).hasSize(1);
        assertThat(solution.getCustomerList().get(0).getLocation().getId()).isEqualTo(location2.id());
    }

    @Test
    void solver_should_not_start_nor_stop_when_modifying_location_and_there_are_no_vehicles() {
        // add 2 locations
        routeOptimizer.addLocation(location1, distanceMatrix);
        clearInvocations(solutionPublisher);
        routeOptimizer.addLocation(location2, distanceMatrix);

        // solving did not start due to missing vehicles
        verify(solverManager, never()).startSolver(any());
        // but preliminary solution is published
        VehicleRoutingSolution solution1 = verifyPublishingPreliminarySolution();
        assertThat(solution1.getVehicleList()).isEmpty();
        assertThat(solution1.getLocationList()).hasSize(2);

        // add a third location and remove another one
        routeOptimizer.addLocation(location3, distanceMatrix);
        clearInvocations(solutionPublisher);
        routeOptimizer.removeLocation(location2);

        // no interactions with solver (start/stop/problem fact changes) because
        // it hasn't started (due to missing vehicles)
        verifyZeroInteractions(solverManager);
        // but preliminary solution is published
        VehicleRoutingSolution solution2 = verifyPublishingPreliminarySolution();
        assertThat(solution2.getVehicleList()).isEmpty();
        assertThat(solution2.getLocationList()).hasSize(2);
    }

    @Test
    void solver_should_stop_and_publish_when_last_vehicle_is_removed() {
        Vehicle vehicle = vehicle(23);
        routeOptimizer.addVehicle(vehicle);
        routeOptimizer.addLocation(location1, distanceMatrix);
        routeOptimizer.addLocation(location2, distanceMatrix);
        verify(solverManager).startSolver(any(VehicleRoutingSolution.class));
        clearInvocations(solutionPublisher);

        routeOptimizer.removeVehicle(vehicle);
        verify(solverManager).stopSolver();
        VehicleRoutingSolution solution = verifyPublishingPreliminarySolution();
        assertThat(solution.getVehicleList()).isEmpty();
    }

    @Test
    void solver_should_stop_when_locations_reduced_to_one() {
        // add 1 vehicle, 2 locations
        routeOptimizer.addVehicle(vehicle(0));
        routeOptimizer.addLocation(location1, distanceMatrix);
        routeOptimizer.addLocation(location2, distanceMatrix);
        verify(solverManager).startSolver(any(VehicleRoutingSolution.class));
        clearInvocations(solutionPublisher);

        // remove 1 location from running solver
        routeOptimizer.removeLocation(location2);

        verify(solverManager).stopSolver();

        VehicleRoutingSolution solution = verifyPublishingPreliminarySolution();
        assertThat(solution.getCustomerList()).isEmpty();
        assertThat(solution.getLocationList()).hasSize(1);
        assertThat(solution.getVehicleList()).hasSize(1);
    }

    @Test
    void solution_update_event_should_only_have_empty_routes_when_last_visit_removed() {
        // FIXME This test shouldn't be needed. This is a problem with bad encapsulation of the planning domain in
        //   optaplanner-examples. Once we introduce our own planning domain with a better API, the test should be
        //   replaced/simplified/removed.

        // Prepare a solution with 1 depot, 2 vehicles, 1 customer and both vehicles visiting to that customer
        final long vehicleId1 = 1;
        final long vehicleId2 = 2;
        routeOptimizer.addVehicle(vehicle(vehicleId1));
        routeOptimizer.addVehicle(vehicle(vehicleId2));

        // Start solver by adding two locations
        routeOptimizer.addLocation(location1, distanceMatrix);
        routeOptimizer.addLocation(location2, distanceMatrix);

        verify(solverManager).startSolver(any(VehicleRoutingSolution.class));
        clearInvocations(solutionPublisher);

        routeOptimizer.removeLocation(location2);
        verify(solverManager).stopSolver();

        // no customer -> all routes should be empty
        VehicleRoutingSolution solution = verifyPublishingPreliminarySolution();
        assertThat(solution.getDistanceString(null)).isEqualTo("0h 0m 0s 0ms"); // expect zero travel distance
        assertThat(solution.getVehicleList())
                .extracting(AbstractPersistable::getId)
                .containsExactlyInAnyOrder(vehicleId1, vehicleId2);
        assertThat(solution.getDepotList()).extracting(Depot::getId).containsExactly(location1.id());
    }

    @Test
    void removing_depot_impossible_when_there_are_other_locations() {
        routeOptimizer.addVehicle(vehicle(0));
        // add 2 locations
        routeOptimizer.addLocation(location1, distanceMatrix);
        routeOptimizer.addLocation(location2, distanceMatrix);
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
        routeOptimizer.addVehicle(vehicle(vehicleId1));
        routeOptimizer.addVehicle(vehicle(vehicleId2));
        clearInvocations(solutionPublisher);

        // when a depot is added
        routeOptimizer.addLocation(location1, distanceMatrix);

        // then all vehicles must be in the depot
        VehicleRoutingSolution solution1 = verifyPublishingPreliminarySolution();
        assertThat(solution1.getVehicleList())
                .extracting(AbstractPersistable::getId)
                .containsExactlyInAnyOrder(vehicleId1, vehicleId2);
        assertThat(solution1.getVehicleList()).allMatch(vehicle -> vehicle.getDepot().getId().equals(location1.id()));
        assertThat(solution1.getDepotList()).extracting(AbstractPersistable::getId).containsExactly(location1.id());

        // if we remove the depot
        clearInvocations(solutionPublisher);
        routeOptimizer.removeLocation(location1);

        // then published solution's depot list is empty
        VehicleRoutingSolution solution2 = verifyPublishingPreliminarySolution();
        assertThat(solution2.getVehicleList())
                .extracting(AbstractPersistable::getId)
                .containsExactlyInAnyOrder(vehicleId1, vehicleId2);
        assertThat(solution2.getDepotList()).isEmpty();

        // and it's possible to add a new depot
        routeOptimizer.addLocation(location2, distanceMatrix);
    }

    @Test
    void adding_location_to_running_solver_must_happen_through_problem_fact_change() {
        // arrange
        routeOptimizer.addVehicle(vehicle(55));
        routeOptimizer.addLocation(location1, distanceMatrix);
        routeOptimizer.addLocation(location2, distanceMatrix);
        verify(solverManager).startSolver(any(VehicleRoutingSolution.class));
        // act
        routeOptimizer.addLocation(location3, distanceMatrix);
        // assert
        verify(solverManager).addLocation(any(RoadLocation.class));
    }

    @Test
    void removing_location_from_solver_with_more_than_two_locations_must_happen_through_problem_fact_change() {
        // arrange: set up a situation where solver is running with 1 depot and 2 visits
        long vehicleId = 0;
        routeOptimizer.addVehicle(vehicle(vehicleId));
        routeOptimizer.addLocation(location1, distanceMatrix);
        routeOptimizer.addLocation(location2, distanceMatrix);
        verify(solverManager).startSolver(any(VehicleRoutingSolution.class));

        // add second visit to avoid stopping solver manager after removing a visit below
        routeOptimizer.addLocation(location3, distanceMatrix);
        verify(solverManager).addLocation(any(org.optaplanner.examples.vehiclerouting.domain.location.Location.class));

        // act
        routeOptimizer.removeLocation(location2);

        // assert
        ArgumentCaptor<RoadLocation> roadLocationArgumentCaptor = ArgumentCaptor.forClass(RoadLocation.class);
        verify(solverManager).removeLocation(roadLocationArgumentCaptor.capture());
        assertThat(roadLocationArgumentCaptor.getValue().getId()).isEqualTo(location2.id());
        // solver still running
        verify(solverManager, never()).stopSolver();
    }

    @Test
    void adding_vehicle_to_running_solver_must_happen_through_problem_fact_change() {
        // arrange
        routeOptimizer.addVehicle(vehicle(1));
        routeOptimizer.addLocation(location1, distanceMatrix);
        routeOptimizer.addLocation(location2, distanceMatrix);
        verify(solverManager).startSolver(any(VehicleRoutingSolution.class));

        // act
        routeOptimizer.addVehicle(vehicle(22));

        // assert
        verify(solverManager).addVehicle(vehicleArgumentCaptor.capture());
        org.optaplanner.examples.vehiclerouting.domain.Vehicle vehicle = vehicleArgumentCaptor.getValue();
        assertThat(vehicle.getDepot().getId()).isEqualTo(location1.id());
    }

    @Test
    void removing_vehicle_from_running_solver_with_more_than_one_vehicle_must_happen_through_problem_fact_change() {
        // arrange: set up a situation where solver is running with 2 vehicles
        final long vehicleId1 = 10;
        final long vehicleId2 = 20;
        routeOptimizer.addVehicle(vehicle(vehicleId1));
        routeOptimizer.addVehicle(vehicle(vehicleId2));
        routeOptimizer.addLocation(location1, distanceMatrix);
        routeOptimizer.addLocation(location2, distanceMatrix);
        verify(solverManager).startSolver(any(VehicleRoutingSolution.class));

        // act
        routeOptimizer.removeVehicle(vehicle(vehicleId1));

        // assert
        verify(solverManager).removeVehicle(any(org.optaplanner.examples.vehiclerouting.domain.Vehicle.class));
        // solver still running
        verify(solverManager, never()).stopSolver();
    }

    @Test
    void changing_vehicle_capacity_should_take_effect_when_solver_is_started_or_be_published() {
        // 1 depot, 1 vehicle
        final int capacity = VehicleFactory.DEFAULT_VEHICLE_CAPACITY * 2 + 29;
        final long vehicleId1 = 10;
        routeOptimizer.addVehicle(vehicle(vehicleId1));
        routeOptimizer.addLocation(location1, distanceMatrix);
        clearInvocations(solutionPublisher);

        // change capacity when solver is not running
        routeOptimizer.changeCapacity(vehicle(vehicleId1), capacity);
        verifyZeroInteractions(solverManager);
        VehicleRoutingSolution preliminarySolution = verifyPublishingPreliminarySolution();
        assertThat(preliminarySolution.getVehicleList().get(0).getCapacity()).isEqualTo(capacity);

        // start solver
        routeOptimizer.addLocation(location2, distanceMatrix);

        VehicleRoutingSolution solution = verifySolverStartedWithSolution();
        assertThat(solution.getVehicleList().get(0).getCapacity()).isEqualTo(capacity);
    }

    @Test
    void changing_vehicle_capacity_must_happen_through_problem_fact_change_when_solver_is_running() {
        // 1 vehicle, 1 depot, 1 visit
        final int capacity = 14816;
        final long vehicleId1 = 10;
        routeOptimizer.addVehicle(vehicle(vehicleId1));
        routeOptimizer.addLocation(location1, distanceMatrix);
        routeOptimizer.addLocation(location2, distanceMatrix);
        verify(solverManager).startSolver(any(VehicleRoutingSolution.class));

        routeOptimizer.changeCapacity(vehicle(vehicleId1), capacity);

        verify(solverManager).changeCapacity(any(org.optaplanner.examples.vehiclerouting.domain.Vehicle.class));
    }

    @Test
    void changing_vehicle_capacity_must_fail_fast_if_the_vehicle_does_not_exist() {
        // 1 vehicle, 1 depot, 1 visit
        final long vehicleId = 10;
        routeOptimizer.addVehicle(vehicle(vehicleId));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> routeOptimizer.changeCapacity(vehicle(vehicleId + 1), 0))
                .withMessageContaining("exist");
    }

    @Test
    void remove_all_locations_should_stop_solver_and_publish_preliminary_solution() {
        // set up a situation where solver is running with 1 depot and 2 visits
        long vehicleId = 10;
        routeOptimizer.addVehicle(vehicle(vehicleId));
        routeOptimizer.addLocation(location1, distanceMatrix);
        routeOptimizer.addLocation(location2, distanceMatrix);
        verify(solverManager).startSolver(any(VehicleRoutingSolution.class));
        routeOptimizer.addLocation(location3, distanceMatrix);
        clearInvocations(solutionPublisher);

        routeOptimizer.removeAllLocations();

        verify(solverManager).stopSolver();

        VehicleRoutingSolution solution = verifyPublishingPreliminarySolution();
        assertThat(solution.getVehicleList()).hasSize(1);
        assertThat(solution.getDepotList()).isEmpty();
        assertThat(solution.getCustomerList()).isEmpty();
        assertThat(solution.getLocationList()).isEmpty();
    }

    @Test
    void remove_all_vehicles_should_stop_solver_and_publish_preliminary_solution() {
        long vehicleId = 10;
        routeOptimizer.addVehicle(vehicle(vehicleId));
        routeOptimizer.addLocation(location1, distanceMatrix);
        routeOptimizer.addLocation(location2, distanceMatrix);
        verify(solverManager).startSolver(any(VehicleRoutingSolution.class));
        routeOptimizer.addLocation(location3, distanceMatrix);
        clearInvocations(solutionPublisher);

        routeOptimizer.removeAllVehicles();

        verify(solverManager).stopSolver();

        VehicleRoutingSolution solution = verifyPublishingPreliminarySolution();
        assertThat(solution.getVehicleList()).isEmpty();
        assertThat(solution.getDepotList()).hasSize(1);
        assertThat(solution.getCustomerList()).hasSize(2);
        assertThat(solution.getLocationList()).hasSize(3);
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
        verify(solutionPublisher).publishSolution(solutionArgumentCaptor.capture());
        return solutionArgumentCaptor.getValue();
    }

    private VehicleRoutingSolution verifySolverStartedWithSolution() {
        verify(solverManager).startSolver(solutionArgumentCaptor.capture());
        return solutionArgumentCaptor.getValue();
    }

    private static Vehicle vehicle(long id) {
        return new Vehicle(id, "");
    }
}
