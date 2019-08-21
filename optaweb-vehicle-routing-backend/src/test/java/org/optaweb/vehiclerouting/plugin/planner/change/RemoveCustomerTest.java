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
import org.optaplanner.examples.vehiclerouting.domain.Vehicle;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.domain.location.Location;
import org.optaplanner.examples.vehiclerouting.domain.location.RoadLocation;
import org.optaweb.vehiclerouting.plugin.planner.SolutionFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoveCustomerTest {

    @Mock
    private ScoreDirector<VehicleRoutingSolution> scoreDirector;

    @Test
    void remove_last_customer() {
        VehicleRoutingSolution solution = SolutionFactory.emptySolution();
        when(scoreDirector.getWorkingSolution()).thenReturn(solution);

        Customer removedCustomer = customer(1);
        Customer otherCustomer = customer(2);
        solution.getCustomerList().add(otherCustomer);
        solution.getCustomerList().add(removedCustomer);

        // V -> other -> removed
        otherCustomer.setPreviousStandstill(new Vehicle());
        otherCustomer.setNextCustomer(removedCustomer);
        removedCustomer.setPreviousStandstill(otherCustomer);

        when(scoreDirector.lookUpWorkingObject(removedCustomer)).thenReturn(removedCustomer);

        // do change
        RemoveCustomer removeCustomer = new RemoveCustomer(removedCustomer);
        removeCustomer.doChange(scoreDirector);

        verify(scoreDirector).beforeEntityRemoved(any(Customer.class));
        verify(scoreDirector).afterEntityRemoved(any(Customer.class));
        assertThat(solution.getCustomerList()).containsExactly(otherCustomer);

        verify(scoreDirector).triggerVariableListeners();
    }

    @Test
    void remove_middle_customer() {
        VehicleRoutingSolution solution = SolutionFactory.emptySolution();
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

        when(scoreDirector.lookUpWorkingObject(removedCustomer)).thenReturn(removedCustomer);

        // do change
        RemoveCustomer removeCustomer = new RemoveCustomer(removedCustomer);
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

    @Test
    void fail_fast_if_working_solution_customer_list_does_not_contain_working_customer() {
        VehicleRoutingSolution solution = SolutionFactory.emptySolution();

        long removedId = 111L;
        Customer removedCustomer = customer(removedId);
        long wrongId = 222L;
        Customer wrongCustomer = customer(wrongId);
        wrongCustomer.setPreviousStandstill(new Vehicle());
        solution.getCustomerList().add(wrongCustomer);

        when(scoreDirector.getWorkingSolution()).thenReturn(solution);
        when(scoreDirector.lookUpWorkingObject(removedCustomer)).thenReturn(removedCustomer);

        // do change
        RemoveCustomer removeCustomer = new RemoveCustomer(removedCustomer);
        assertThatIllegalStateException()
                .isThrownBy(() -> removeCustomer.doChange(scoreDirector))
                .withMessageMatching(".*List .*" + wrongId + ".* doesn't contain the working.*" + removedId + ".*");
    }

    @Test
    void fail_fast_if_working_object_is_null() {
        when(scoreDirector.getWorkingSolution()).thenReturn(SolutionFactory.emptySolution());

        assertThatIllegalStateException()
                .isThrownBy(() -> new RemoveCustomer(customer(0)).doChange(scoreDirector))
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
