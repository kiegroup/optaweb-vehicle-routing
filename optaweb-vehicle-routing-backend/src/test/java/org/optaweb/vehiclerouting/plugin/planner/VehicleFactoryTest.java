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
import org.optaweb.vehiclerouting.domain.Vehicle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.optaweb.vehiclerouting.plugin.planner.VehicleFactory.fromDomain;

class VehicleFactoryTest {

    @Test
    void planning_vehicle() {
        long vehicleId = 2;
        String name = "not used";
        Vehicle domainVehicle = new Vehicle(vehicleId, name);

        org.optaplanner.examples.vehiclerouting.domain.Vehicle vehicle = fromDomain(domainVehicle);

        assertThat(vehicle.getId()).isEqualTo(vehicleId);
        assertThat(vehicle.getCapacity()).isEqualTo(VehicleFactory.DEFAULT_VEHICLE_CAPACITY);
    }
}
