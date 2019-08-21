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

package org.optaweb.vehiclerouting.plugin.planner.change;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.Depot;
import org.optaplanner.examples.vehiclerouting.domain.Vehicle;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.domain.location.Location;
import org.optaplanner.examples.vehiclerouting.domain.location.RoadLocation;
import org.optaweb.vehiclerouting.plugin.planner.SolutionFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoveVehicleTest {

    @Mock
    private ScoreDirector<VehicleRoutingSolution> scoreDirector;

    @Test
    void remove_vehicle() {
        VehicleRoutingSolution solution = SolutionFactory.emptySolution();
        when(scoreDirector.getWorkingSolution()).thenReturn(solution);

        Location location = new RoadLocation(1, 2.0, 3.0);
        Depot depot = new Depot();
        depot.setLocation(location);

        Vehicle removedVehicle = new Vehicle();
        removedVehicle.setId(1L);
        removedVehicle.setDepot(depot);
        Vehicle otherVehicle = new Vehicle();
        otherVehicle.setId(2L);
        otherVehicle.setDepot(depot);
        solution.getVehicleList().add(removedVehicle);
        solution.getVehicleList().add(otherVehicle);

        when(scoreDirector.lookUpWorkingObject(removedVehicle)).thenReturn(removedVehicle);

        Customer firstCustomer = customer(1);
        Customer lastCustomer = customer(2);
        solution.getCustomerList().add(firstCustomer);
        solution.getCustomerList().add(lastCustomer);

        // V -> first -> last
        removedVehicle.setNextCustomer(firstCustomer);
        firstCustomer.setPreviousStandstill(removedVehicle);
        firstCustomer.setVehicle(removedVehicle);
        firstCustomer.setNextCustomer(lastCustomer);
        lastCustomer.setPreviousStandstill(firstCustomer);
        lastCustomer.setVehicle(removedVehicle);

        // do change
        RemoveVehicle removeVehicle = new RemoveVehicle(removedVehicle);
        removeVehicle.doChange(scoreDirector);

        assertThat(firstCustomer.getPreviousStandstill()).isNull();
        assertThat(lastCustomer.getPreviousStandstill()).isNull();
        assertThat(solution.getVehicleList()).containsExactly(otherVehicle);

        verify(scoreDirector).beforeVariableChanged(firstCustomer, "previousStandstill");
        verify(scoreDirector).afterVariableChanged(firstCustomer, "previousStandstill");
        verify(scoreDirector).beforeVariableChanged(lastCustomer, "previousStandstill");
        verify(scoreDirector).afterVariableChanged(lastCustomer, "previousStandstill");
        verify(scoreDirector).beforeProblemFactRemoved(any(Vehicle.class));
        verify(scoreDirector).afterProblemFactRemoved(any(Vehicle.class));
        verify(scoreDirector).triggerVariableListeners();
    }

    @Test
    void fail_fast_if_working_solution_vehicle_list_does_not_contain_working_vehicle() {
        VehicleRoutingSolution solution = SolutionFactory.emptySolution();

        Location location = new RoadLocation(1, 2.0, 3.0);
        Depot depot = new Depot();
        depot.setLocation(location);

        long removedId = 111L;
        Vehicle removedVehicle = new Vehicle();
        removedVehicle.setId(removedId);
        removedVehicle.setDepot(depot);
        long wrongId = 222L;
        Vehicle wrongVehicle = new Vehicle();
        wrongVehicle.setId(wrongId);
        wrongVehicle.setDepot(depot);
        solution.getVehicleList().add(wrongVehicle);

        when(scoreDirector.getWorkingSolution()).thenReturn(solution);
        when(scoreDirector.lookUpWorkingObject(removedVehicle)).thenReturn(removedVehicle);

        // do change
        RemoveVehicle removeVehicle = new RemoveVehicle(removedVehicle);
        assertThatIllegalStateException()
                .isThrownBy(() -> removeVehicle.doChange(scoreDirector))
                .withMessageMatching(".*List .*" + wrongId + ".* doesn't contain the working.*" + removedId + ".*");
    }

    @Test
    void fail_fast_if_working_object_is_null() {
        when(scoreDirector.getWorkingSolution()).thenReturn(SolutionFactory.emptySolution());
        Depot depot = new Depot();
        depot.setLocation(new RoadLocation(4L, 1, 2));
        Vehicle vehicle = new Vehicle();
        vehicle.setId(1L);
        vehicle.setDepot(depot);

        assertThatIllegalStateException()
                .isThrownBy(() -> new RemoveVehicle(vehicle).doChange(scoreDirector))
                .withMessageContaining("working copy of");
    }

    private static Customer customer(long id) {
        Location location = new RoadLocation(1000000 + id, id, id);
        Customer customer = new Customer();
        customer.setId(id);
        customer.setLocation(location);
        return customer;
    }
}
