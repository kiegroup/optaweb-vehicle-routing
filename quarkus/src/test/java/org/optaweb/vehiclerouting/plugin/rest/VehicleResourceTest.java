/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.optaweb.vehiclerouting.plugin.rest;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.optaweb.vehiclerouting.service.vehicle.VehicleService;

@ExtendWith(MockitoExtension.class)
class VehicleResourceTest {

    @Mock
    private VehicleService vehicleService;
    @InjectMocks
    private VehicleResource vehicleResource;

    @Test
    void addVehicle() {
        vehicleResource.addVehicle();
        verify(vehicleService).createVehicle();
    }

    @Test
    void removeVehicle() {
        vehicleResource.removeVehicle(11L);
        verify(vehicleService).removeVehicle(11);
    }

    @Test
    void removeAnyVehicle() {
        vehicleResource.removeAnyVehicle();
        verify(vehicleService).removeAnyVehicle();
    }

    @Test
    void changeCapacity() {
        long vehicleId = 2000;
        int capacity = 50;
        vehicleResource.changeCapacity(vehicleId, capacity);
        verify(vehicleService).changeCapacity(vehicleId, capacity);
    }
}
