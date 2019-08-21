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
import org.optaplanner.examples.vehiclerouting.domain.location.RoadLocation;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerFactoryTest {

    @Test
    void customer_should_have_location_id_and_default_demand() {
        long id = 4;
        RoadLocation location = new RoadLocation(id, 1.0, 2.0);

        Customer customer = CustomerFactory.customer(location);

        assertThat(customer.getId()).isEqualTo(location.getId());
        assertThat(customer.getLocation()).isEqualTo(location);
        assertThat(customer.getDemand()).isEqualTo(CustomerFactory.DEFAULT_CUSTOMER_DEMAND);
    }
}
