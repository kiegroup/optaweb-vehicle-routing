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

package org.optaweb.vehiclerouting.plugin.planner.domain;

import org.junit.jupiter.api.Test;
import org.optaweb.vehiclerouting.domain.Vehicle;
import org.optaweb.vehiclerouting.domain.VehicleFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVehicleFactory.fromDomain;

class PlanningVehicleFactoryTest {

    @Test
    void planning_vehicle() {
        long vehicleId = 2;
        String name = "not used";
        int capacity = 7;
        Vehicle domainVehicle = VehicleFactory.createVehicle(vehicleId, name, capacity);

        PlanningVehicle vehicle = fromDomain(domainVehicle);

        assertThat(vehicle.getId()).isEqualTo(vehicleId);
        assertThat(vehicle.getCapacity()).isEqualTo(capacity);
    }
}
