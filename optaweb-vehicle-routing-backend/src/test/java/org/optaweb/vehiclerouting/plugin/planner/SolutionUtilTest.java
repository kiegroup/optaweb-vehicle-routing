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
import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.Location;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.optaweb.vehiclerouting.plugin.planner.SolutionUtil.createSolution;
import static org.optaweb.vehiclerouting.plugin.planner.SolutionUtil.planningLocation;

class SolutionUtilTest {

    @Test
    void empty_solution_should_be_empty() {
        VehicleRoutingSolution solution = SolutionUtil.emptySolution();
        assertThat(solution.getLocationList()).isEmpty();
        assertThat(solution.getCustomerList()).isEmpty();
        assertThat(solution.getDepotList()).isEmpty();
        assertThat(solution.getVehicleList()).isEmpty();
        assertThat(solution.getDistanceUnitOfMeasurement()).isEqualTo("sec");
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
    void planning_location_should_have_same_latitude_and_longitude() {
        Location domainLocation = new Location(1, Coordinates.valueOf(1.0, 0.1));
        RoadLocation roadLocation = planningLocation(domainLocation);
        assertThat(roadLocation.getId()).isEqualTo(domainLocation.id());
        assertThat(roadLocation.getLatitude()).isEqualTo(domainLocation.coordinates().latitude().doubleValue());
        assertThat(roadLocation.getLongitude()).isEqualTo(domainLocation.coordinates().longitude().doubleValue());
    }

    @Test
    void solution_created_from_vehicles_depot_and_visits_should_be_consistent() {
        RoadLocation depotLocation = new RoadLocation(1, 1, 1);
        Depot depot = new Depot();
        depot.setLocation(depotLocation);

        RoadLocation visitLocation = new RoadLocation(2, 2, 2);

        Vehicle vehicle = new Vehicle();

        VehicleRoutingSolution solutionWithDepot = createSolution(
                singletonList(vehicle),
                depot,
                singletonList(visitLocation)
        );
        assertThat(solutionWithDepot.getVehicleList()).containsExactly(vehicle);
        assertThat(vehicle.getDepot()).isEqualTo(depot);
        assertThat(solutionWithDepot.getDepotList()).containsExactly(depot);
        assertThat(solutionWithDepot.getCustomerList()).hasSize(1);
        assertThat(solutionWithDepot.getCustomerList().get(0).getLocation()).isEqualTo(visitLocation);
        assertThat(solutionWithDepot.getLocationList()).containsExactlyInAnyOrder(depotLocation, visitLocation);

        VehicleRoutingSolution solutionWithNoDepot = createSolution(
                singletonList(vehicle),
                null,
                singletonList(visitLocation)
        );
        assertThat(solutionWithNoDepot.getDepotList()).isEmpty();
    }
}
