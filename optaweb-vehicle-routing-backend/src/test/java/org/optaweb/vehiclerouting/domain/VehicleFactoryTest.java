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

package org.optaweb.vehiclerouting.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VehicleFactoryTest {

    @Test
    void createVehicle() {
        long vehicleId = 4;
        String name = "Vehicle four";
        int capacity = 99;

        Vehicle vehicle = VehicleFactory.createVehicle(vehicleId, name, capacity);

        assertThat(vehicle.id()).isEqualTo(vehicleId);
        assertThat(vehicle.name()).isEqualTo(name);
        assertThat(vehicle.capacity()).isEqualTo(capacity);
    }
}
