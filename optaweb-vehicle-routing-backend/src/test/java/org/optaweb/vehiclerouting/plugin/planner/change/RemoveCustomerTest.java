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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.Vehicle;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.domain.location.Location;
import org.optaplanner.examples.vehiclerouting.domain.location.RoadLocation;
import org.optaweb.vehiclerouting.plugin.planner.RouteOptimizerImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RemoveCustomerTest {

    @Mock
    private ScoreDirector<VehicleRoutingSolution> scoreDirector;

    @Test
    public void remove_last_customer() {
        VehicleRoutingSolution solution = RouteOptimizerImpl.emptySolution();
        when(scoreDirector.getWorkingSolution()).thenReturn(solution);

        Customer removedCustomer = customer(1);
        Customer otherCustomer = customer(2);
        solution.getCustomerList().add(otherCustomer);
        solution.getCustomerList().add(removedCustomer);

        // V -> other -> removed
        otherCustomer.setPreviousStandstill(new Vehicle());
        otherCustomer.setNextCustomer(removedCustomer);
        removedCustomer.setPreviousStandstill(otherCustomer);

        // do change
        RemoveCustomer removeCustomer = new RemoveCustomer(removedCustomer.getLocation());
        removeCustomer.doChange(scoreDirector);

        verify(scoreDirector).beforeEntityRemoved(any(Customer.class));
        verify(scoreDirector).afterEntityRemoved(any(Customer.class));
        assertThat(solution.getCustomerList()).containsExactly(otherCustomer);

        verify(scoreDirector).triggerVariableListeners();
    }

    @Test
    public void remove_middle_customer() {
        VehicleRoutingSolution solution = RouteOptimizerImpl.emptySolution();
        when(scoreDirector.getWorkingSolution()).thenReturn(solution);

        Customer firstCustomer = customer(1);
        Customer removedCustomer = customer(2);
        Customer lastCustomer = customer(3);
        solution.getCustomerList().add(firstCustomer);
        solution.getCustomerList().add(lastCustomer);
        solution.getCustomerList().add(removedCustomer);

        // V -> first -> removed -> last
        firstCustomer.setPreviousStandstill(new Vehicle());
        firstCustomer.setNextCustomer(removedCustomer);
        removedCustomer.setPreviousStandstill(firstCustomer);
        removedCustomer.setNextCustomer(lastCustomer);
        lastCustomer.setPreviousStandstill(removedCustomer);

        // do change
        RemoveCustomer removeCustomer = new RemoveCustomer(removedCustomer.getLocation());
        removeCustomer.doChange(scoreDirector);

        // TODO make this more accurate once Customer overrides equals()
        verify(scoreDirector).beforeVariableChanged(any(Customer.class), anyString());
        verify(scoreDirector).afterVariableChanged(any(Customer.class), anyString());
        verify(scoreDirector).beforeEntityRemoved(any(Customer.class));
        verify(scoreDirector).afterEntityRemoved(any(Customer.class));
        assertThat(solution.getCustomerList())
                .hasSize(2)
                .containsOnly(firstCustomer, lastCustomer);

        // V -> first -> removed -> last
        assertThat(lastCustomer.getPreviousStandstill()).isEqualTo(firstCustomer);

        verify(scoreDirector).triggerVariableListeners();
    }

    private static Customer customer(long id) {
        Location location = new RoadLocation(1000000 + id, id, id);
        Customer customer = new Customer();
        customer.setId(id);
        customer.setLocation(location);
        return customer;
    }
}
