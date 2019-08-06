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
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.domain.location.Location;
import org.optaplanner.examples.vehiclerouting.domain.location.RoadLocation;
import org.optaweb.vehiclerouting.plugin.planner.CustomerFactory;
import org.optaweb.vehiclerouting.plugin.planner.SolutionFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddCustomerTest {

    @Mock
    private ScoreDirector<VehicleRoutingSolution> scoreDirector;

    @Test
    void add_customer_should_add_location_and_create_customer() {
        VehicleRoutingSolution solution = SolutionFactory.emptySolution();
        when(scoreDirector.getWorkingSolution()).thenReturn(solution);

        Location location = new RoadLocation(1, 1.0, 2.0);
        AddCustomer addCustomer = new AddCustomer(location);
        addCustomer.doChange(scoreDirector);

        verify(scoreDirector).beforeProblemFactAdded(location);
        verify(scoreDirector).afterProblemFactAdded(location);
        assertThat(solution.getLocationList()).containsExactly(location);

        verify(scoreDirector).beforeEntityAdded(any(Customer.class));
        verify(scoreDirector).afterEntityAdded(any(Customer.class));
        assertThat(solution.getCustomerList()).hasSize(1);
        Customer customer = solution.getCustomerList().get(0);
        assertThat(customer.getId()).isEqualTo(location.getId());
        assertThat(customer.getLocation()).isEqualTo(location);
        assertThat(customer.getDemand()).isEqualTo(CustomerFactory.DEFAULT_CUSTOMER_DEMAND);

        verify(scoreDirector).triggerVariableListeners();
    }
}
