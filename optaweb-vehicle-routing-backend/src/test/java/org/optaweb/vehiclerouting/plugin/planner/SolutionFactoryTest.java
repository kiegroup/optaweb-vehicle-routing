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

import org.junit.jupiter.api.Test;
import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.Depot;
import org.optaplanner.examples.vehiclerouting.domain.Vehicle;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.domain.location.RoadLocation;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.optaweb.vehiclerouting.plugin.planner.SolutionFactory.solutionFromCustomers;

class SolutionFactoryTest {

    @Test
    void empty_solution_should_be_empty() {
        VehicleRoutingSolution solution = SolutionFactory.emptySolution();
        assertThat(solution.getLocationList()).isEmpty();
        assertThat(solution.getCustomerList()).isEmpty();
        assertThat(solution.getDepotList()).isEmpty();
        assertThat(solution.getVehicleList()).isEmpty();
        assertThat(solution.getDistanceUnitOfMeasurement()).isEqualTo("sec");
    }

    @Test
    void solution_created_from_vehicles_depot_and_visits_should_be_consistent() {
        Vehicle vehicle = new Vehicle();

        RoadLocation depotLocation = new RoadLocation(1, 1, 1);
        Depot depot = new Depot();
        depot.setLocation(depotLocation);

        Customer customer = CustomerFactory.customer(new RoadLocation(2, 2, 2));

        VehicleRoutingSolution solutionWithDepot = solutionFromCustomers(
                singletonList(vehicle),
                depot,
                singletonList(customer)
        );
        assertThat(solutionWithDepot.getVehicleList()).containsExactly(vehicle);
        assertThat(vehicle.getDepot()).isEqualTo(depot);
        assertThat(solutionWithDepot.getDepotList()).containsExactly(depot);
        assertThat(solutionWithDepot.getCustomerList()).hasSize(1);
        assertThat(solutionWithDepot.getCustomerList()).containsExactly(customer);
        assertThat(solutionWithDepot.getCustomerList().get(0).getLocation()).isEqualTo(customer.getLocation());
        assertThat(solutionWithDepot.getLocationList())
                .containsExactlyInAnyOrder(depotLocation, customer.getLocation());

        VehicleRoutingSolution solutionWithNoDepot = solutionFromCustomers(
                singletonList(vehicle),
                null,
                emptyList()
        );
        assertThat(solutionWithNoDepot.getDepotList()).isEmpty();
    }
}
