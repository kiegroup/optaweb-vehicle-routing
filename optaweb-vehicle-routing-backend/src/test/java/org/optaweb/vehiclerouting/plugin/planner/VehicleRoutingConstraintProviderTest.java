/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import org.junit.jupiter.api.Test;
import org.optaplanner.test.api.score.stream.ConstraintVerifier;
import org.optaweb.vehiclerouting.plugin.planner.domain.DistanceMap;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningDepot;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVehicle;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVehicleFactory;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVisit;
import org.optaweb.vehiclerouting.plugin.planner.domain.Standstill;
import org.optaweb.vehiclerouting.plugin.planner.domain.VehicleRoutingSolution;

import static org.optaweb.vehiclerouting.plugin.planner.domain.PlanningLocationFactory.testLocation;
import static org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVisitFactory.fromLocation;

class VehicleRoutingConstraintProviderTest {

    private final ConstraintVerifier<VehicleRoutingConstraintProvider, VehicleRoutingSolution> constraintVerifier =
            ConstraintVerifier.build(
                    new VehicleRoutingConstraintProvider(),
                    VehicleRoutingSolution.class,
                    Standstill.class,
                    PlanningVisit.class
            );

    private static DistanceMap zeroDistanceAll() {
        return location -> 0;
    }

    private static void route(PlanningVehicle vehicle, PlanningVisit... visits) {
        Standstill previousStandstill = vehicle;

        for (PlanningVisit visit : visits) {
            visit.setVehicle(vehicle);
            visit.setPreviousStandstill(previousStandstill);
            previousStandstill.setNextVisit(visit);
            previousStandstill = visit;
        }
    }

    @Test
    void vehicle_capacity_penalized_1vehicle_1visit() {
        int demand = 100;
        int capacity = 5;

        PlanningVehicle vehicle = PlanningVehicleFactory.testVehicle(1, capacity);
        vehicle.setDepot(new PlanningDepot(testLocation(1, zeroDistanceAll())));

        PlanningVisit visit = fromLocation(testLocation(2, zeroDistanceAll()), demand);

        route(vehicle, visit);

        constraintVerifier.verifyThat(VehicleRoutingConstraintProvider::vehicleCapacity)
                .given(visit)
                .penalizesBy(demand - capacity);
    }

    @Test
    void vehicle_capacity_penalized_1vehicle_3visits() {
        int demand1 = 4;
        int demand2 = 3;
        int demand3 = 9;
        int capacity = 5;

        PlanningVehicle vehicle = PlanningVehicleFactory.testVehicle(1, capacity);
        vehicle.setDepot(new PlanningDepot(testLocation(0, zeroDistanceAll())));

        PlanningVisit visit1 = fromLocation(testLocation(1, zeroDistanceAll()), demand1);
        PlanningVisit visit2 = fromLocation(testLocation(2, zeroDistanceAll()), demand2);
        PlanningVisit visit3 = fromLocation(testLocation(3, zeroDistanceAll()), demand3);

        route(vehicle, visit1, visit2, visit3);

        constraintVerifier.verifyThat(VehicleRoutingConstraintProvider::vehicleCapacity)
                .given(visit1, visit2, visit3)
                .penalizesBy(demand1 + demand2 + demand3 - capacity);
    }

    @Test
    void capacity_not_penalized_when_greater_or_equal_to_demand() {
        int demand1 = 4;
        int demand2 = 3;
        int demand3 = 9;
        int totalDemand = demand1 + demand2 + demand3;

        PlanningVehicle vehicle = PlanningVehicleFactory.testVehicle(1, totalDemand);
        vehicle.setDepot(new PlanningDepot(testLocation(0, zeroDistanceAll())));

        PlanningVisit visit1 = fromLocation(testLocation(1, zeroDistanceAll()), demand1);
        PlanningVisit visit2 = fromLocation(testLocation(2, zeroDistanceAll()), demand2);
        PlanningVisit visit3 = fromLocation(testLocation(3, zeroDistanceAll()), demand3);

        route(vehicle, visit1, visit2, visit3);

        constraintVerifier.verifyThat(VehicleRoutingConstraintProvider::vehicleCapacity)
                .given(visit1, visit2, visit3)
                .penalizesBy(0);

        // test values near the constraint boundary
        vehicle.setCapacity(totalDemand + 1);
        constraintVerifier.verifyThat(VehicleRoutingConstraintProvider::vehicleCapacity)
                .given(visit1, visit2, visit3)
                .penalizesBy(0);
    }

    @Test
    void vehicles_capacity_constraint_should_work_for_multiple_vehicles() {
        int demand1a = 11;
        int demand1b = 12;
        int demand1c = 14;
        int demand2a = 3000;
        int demand2b = 2500;
        int demand2c = 8000;
        int capacity1 = demand1a + demand1b + demand1c;
        int capacity2 = demand2a + demand2b + demand2c;

        PlanningDepot depot = new PlanningDepot(testLocation(0, zeroDistanceAll()));

        PlanningVehicle vehicle1 = PlanningVehicleFactory.testVehicle(1, capacity1);
        vehicle1.setDepot(depot);
        PlanningVehicle vehicle2 = PlanningVehicleFactory.testVehicle(2, capacity2);
        vehicle2.setDepot(depot);

        PlanningVisit visit1 = fromLocation(testLocation(1, zeroDistanceAll()), demand1a);
        PlanningVisit visit2 = fromLocation(testLocation(2, zeroDistanceAll()), demand1b);
        PlanningVisit visit3 = fromLocation(testLocation(3, zeroDistanceAll()), demand1c);
        PlanningVisit visit4 = fromLocation(testLocation(4, zeroDistanceAll()), demand2a);
        PlanningVisit visit5 = fromLocation(testLocation(5, zeroDistanceAll()), demand2b);
        PlanningVisit visit6 = fromLocation(testLocation(6, zeroDistanceAll()), demand2c);

        route(vehicle1, visit1, visit2, visit3);
        route(vehicle2, visit4, visit5, visit6);

        constraintVerifier.verifyThat(VehicleRoutingConstraintProvider::vehicleCapacity)
                .given(visit1, visit2, visit3, visit4, visit5, visit6)
                .penalizesBy(0);

        vehicle1.setCapacity(capacity1 - 3);
        vehicle2.setCapacity(capacity2 - 7);

        constraintVerifier.verifyThat(VehicleRoutingConstraintProvider::vehicleCapacity)
                .given(visit1, visit2, visit3, visit4, visit5, visit6)
                .penalizesBy(10);
    }
}