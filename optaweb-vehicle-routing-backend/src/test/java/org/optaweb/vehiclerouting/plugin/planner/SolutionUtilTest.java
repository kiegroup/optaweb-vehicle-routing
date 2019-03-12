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

import org.junit.Test;
import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.Depot;
import org.optaplanner.examples.vehiclerouting.domain.Vehicle;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.domain.location.RoadLocation;
import org.optaweb.vehiclerouting.domain.Route;

import static org.assertj.core.api.Assertions.assertThat;

public class SolutionUtilTest {

    @Test
    public void empty_solution_should_have_zero_routes() {
        VehicleRoutingSolution solution = RouteOptimizerImpl.emptySolution();
        List<Route> routes = SolutionUtil.routes(solution);
        assertThat(routes).isEmpty();
    }

    @Test
    public void nonempty_uninitialized_solution_should_have_zero_routes() {
        VehicleRoutingSolution solution = RouteOptimizerImpl.emptySolution();

        RoadLocation roadLocation1 = new RoadLocation(1, 1.0, 1.0);
        RoadLocation roadLocation2 = new RoadLocation(2, 2.0, 2.0);
        solution.getLocationList().add(roadLocation1);
        solution.getLocationList().add(roadLocation2);

        Depot depot = new Depot();
        depot.setLocation(roadLocation1);
        solution.getDepotList().add(depot);

        Customer customer = new Customer();
        customer.setLocation(roadLocation2);
        solution.getCustomerList().add(customer);

        List<Route> routes = SolutionUtil.routes(solution);
        assertThat(routes).isEmpty();
    }

    @Test
    public void initialized_solution_should_have_one_route_per_vehicle() {
        VehicleRoutingSolution solution = RouteOptimizerImpl.emptySolution();

        RoadLocation roadLocation1 = new RoadLocation(1, 1.0, 1.0);
        RoadLocation roadLocation2 = new RoadLocation(2, 2.0, 2.0);
        solution.getLocationList().add(roadLocation1);
        solution.getLocationList().add(roadLocation2);

        Depot depot = new Depot();
        depot.setLocation(roadLocation1);
        solution.getDepotList().add(depot);

        Customer customer = new Customer();
        customer.setLocation(roadLocation2);
        solution.getCustomerList().add(customer);

        for (Vehicle vehicle : solution.getVehicleList()) {
            vehicle.setDepot(depot);
            vehicle.setNextCustomer(customer);
            customer.setPreviousStandstill(vehicle);
        }

        List<Route> routes = SolutionUtil.routes(solution);
        assertThat(routes).hasSameSizeAs(solution.getVehicleList());
    }
}
