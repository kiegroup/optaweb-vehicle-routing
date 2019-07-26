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

import org.junit.jupiter.api.Test;
import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.Depot;
import org.optaplanner.examples.vehiclerouting.domain.Vehicle;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.domain.location.RoadLocation;
import org.optaweb.vehiclerouting.service.route.ShallowRoute;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

class SolutionUtilTest {

    @Test
    void empty_solution_should_be_empty() {
        VehicleRoutingSolution solution = SolutionUtil.emptySolution();
        assertThat(solution.getLocationList()).isEmpty();
        assertThat(solution.getCustomerList()).isEmpty();
        assertThat(solution.getDepotList()).isEmpty();
        assertThat(solution.getVehicleList()).isEmpty();
    }

    @Test
    void adding_depot_should_create_depot_and_add_location() {
        VehicleRoutingSolution solution = SolutionUtil.emptySolution();
        RoadLocation roadLocation = new RoadLocation(1, 1.0, 1.0);
        Depot depot = SolutionUtil.addDepot(solution, roadLocation);
        assertThat(solution.getLocationList()).containsExactly(roadLocation);
        assertThat(solution.getDepotList()).containsExactly(depot);
        assertThat(solution.getDepotList().get(0).getLocation()).isSameAs(roadLocation);
    }

    @Test
    void move_all_vehicles_to_a_depot() {
        VehicleRoutingSolution solution = SolutionUtil.emptySolution();
        RoadLocation roadLocation = new RoadLocation(1, 1.0, 1.0);
        Depot depot = SolutionUtil.addDepot(solution, roadLocation);

        SolutionUtil.addVehicle(solution, 1);
        SolutionUtil.addVehicle(solution, 2);
        SolutionUtil.moveAllVehiclesTo(solution, depot);

        assertThat(solution.getVehicleList()).allMatch(vehicle -> vehicle.getDepot() == depot);
    }

    @Test
    void adding_customer_should_create_customer_and_add_location() {
        VehicleRoutingSolution solution = SolutionUtil.emptySolution();
        RoadLocation roadLocation = new RoadLocation(1, 1.0, 1.0);
        Customer customer = SolutionUtil.addCustomer(solution, roadLocation);
        assertThat(customer.getDemand()).isEqualTo(0);
        assertThat(solution.getLocationList()).containsExactly(roadLocation);
        assertThat(solution.getCustomerList()).containsExactly(customer);
        assertThat(solution.getCustomerList().get(0).getLocation()).isSameAs(roadLocation);
    }

    @Test
    void empty_solution_should_have_zero_routes() {
        VehicleRoutingSolution solution = SolutionUtil.emptySolution();
        List<ShallowRoute> routes = SolutionUtil.routes(solution);
        assertThat(routes).isEmpty();
    }

    @Test
    void nonempty_uninitialized_solution_should_have_zero_routes() {
        VehicleRoutingSolution solution = SolutionUtil.emptySolution();

        SolutionUtil.addDepot(solution, new RoadLocation(1, 1.0, 1.0));
        SolutionUtil.addCustomer(solution, new RoadLocation(2, 2.0, 2.0));

        List<ShallowRoute> routes = SolutionUtil.routes(solution);
        assertThat(routes).isEmpty();
    }

    @Test
    void initialized_solution_should_have_one_route_per_vehicle() {
        VehicleRoutingSolution solution = SolutionUtil.emptySolution();
        long vehicleId1 = 1001;
        long vehicleId2 = 2001;
        SolutionUtil.addVehicle(solution, vehicleId1);
        SolutionUtil.addVehicle(solution, vehicleId2);

        Depot depot = SolutionUtil.addDepot(solution, new RoadLocation(1, 1.0, 1.0));
        Customer customer = SolutionUtil.addCustomer(solution, new RoadLocation(2, 2.0, 2.0));

        for (Vehicle vehicle : solution.getVehicleList()) {
            vehicle.setDepot(depot);
            vehicle.setNextCustomer(customer);
            customer.setPreviousStandstill(vehicle);
        }

        List<ShallowRoute> routes = SolutionUtil.routes(solution);
        assertThat(routes).hasSameSizeAs(solution.getVehicleList());
        assertThat(routes.stream().mapToLong(value -> value.vehicleId))
                .containsExactlyInAnyOrder(vehicleId1, vehicleId2);

        for (ShallowRoute route : routes) {
            assertThat(route.depotId).isEqualTo(depot.getId());
            // visits should exclude depot
            assertThat(route.visitIds).hasSize(1).doesNotContain(depot.getId());
        }
    }

    @Test
    void vehicle_without_a_depot_is_illegal() {
        VehicleRoutingSolution solution = SolutionUtil.emptySolution();
        SolutionUtil.addDepot(solution, new RoadLocation(1, 1.0, 1.0));
        SolutionUtil.addVehicle(solution, 1);
        assertThatIllegalStateException().isThrownBy(() -> SolutionUtil.routes(solution));
    }

    @Test
    void fail_fast_if_vehicles_next_customer_doesnt_exist() {
        VehicleRoutingSolution solution = SolutionUtil.emptySolution();
        Depot depot = SolutionUtil.addDepot(solution, new RoadLocation(1, 1.0, 1.0));
        Vehicle vehicle = SolutionUtil.addVehicle(solution, 1);
        SolutionUtil.moveAllVehiclesTo(solution, depot);
        Customer customer = SolutionUtil.addCustomer(solution, new RoadLocation(2, 2.0, 2.0));
        vehicle.setNextCustomer(customer);
        solution.getCustomerList().clear();
        solution.getLocationList().clear();
        assertThatIllegalStateException().isThrownBy(() -> SolutionUtil.routes(solution));
    }
}
