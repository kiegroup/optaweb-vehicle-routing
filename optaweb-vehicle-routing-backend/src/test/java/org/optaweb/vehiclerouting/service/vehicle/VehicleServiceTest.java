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

package org.optaweb.vehiclerouting.service.vehicle;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.optaweb.vehiclerouting.domain.Vehicle;
import org.optaweb.vehiclerouting.service.location.RouteOptimizer;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private RouteOptimizer optimizer;
    @Mock
    private VehicleRepository vehicleRepository;
    @InjectMocks
    private VehicleService vehicleService;

    @Test
    void addVehicle() {
        final Vehicle vehicle = new Vehicle(5, "Test vehicle");
        when(vehicleRepository.createVehicle(anyString())).thenReturn(vehicle);

        vehicleService.addVehicle();

        verify(optimizer).addVehicle(vehicle);
    }

    @Test
    void removeVehicle() {
        final long vehicleId = 8;
        final Vehicle vehicle = new Vehicle(vehicleId, "Removed vehicle");
        when(vehicleRepository.removeVehicle(vehicleId)).thenReturn(vehicle);

        vehicleService.removeVehicle(vehicleId);

        verify(vehicleRepository).removeVehicle(vehicleId);
        verify(optimizer).removeVehicle(vehicle);
    }
}
