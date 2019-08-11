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

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.optaweb.vehiclerouting.domain.Vehicle;
import org.optaweb.vehiclerouting.domain.VehicleFactory;
import org.optaweb.vehiclerouting.service.location.RouteOptimizer;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Captor
    private ArgumentCaptor<Vehicle> vehicleArgumentCaptor;
    @Mock
    private RouteOptimizer optimizer;
    @Mock
    private VehicleRepository vehicleRepository;
    @InjectMocks
    private VehicleService vehicleService;

    @Test
    void addVehicle() {
        final long vehicleId = 63;
        when(vehicleRepository.nextId()).thenReturn(vehicleId);

        final String name = "Veh5";
        final int capacity = VehicleService.DEFAULT_VEHICLE_CAPACITY * 2 + 29;
        final Vehicle vehicle = VehicleFactory.createVehicle(vehicleId, name, capacity);
        // verify that new vehicle is created with correct initial name and capacity
        when(vehicleRepository.createVehicle("Vehicle " + vehicleId, VehicleService.DEFAULT_VEHICLE_CAPACITY))
                .thenReturn(vehicle);

        vehicleService.addVehicle();

        // verify that vehicle provided by repository is passed to optimizer
        verify(optimizer).addVehicle(vehicleArgumentCaptor.capture());
        Vehicle newVehicle = vehicleArgumentCaptor.getValue();
        assertThat(newVehicle.id()).isEqualTo(vehicleId);
        assertThat(newVehicle.name()).isEqualTo(name);
        assertThat(newVehicle.capacity()).isEqualTo(capacity);
    }

    @Test
    void removeVehicle() {
        final long vehicleId = 8;
        final Vehicle vehicle = VehicleFactory.testVehicle(vehicleId);
        when(vehicleRepository.removeVehicle(vehicleId)).thenReturn(vehicle);

        vehicleService.removeVehicle(vehicleId);

        verify(vehicleRepository).removeVehicle(vehicleId);
        verify(optimizer).removeVehicle(vehicle);
    }

    @Test
    void removeAnyVehicle_should_remove_oldest_vehicle() {
        final long vehicleId1 = 1;
        final long vehicleId2 = 2;
        final long vehicleId3 = 3;
        final Vehicle vehicle1 = VehicleFactory.testVehicle(vehicleId1);
        final Vehicle vehicle2 = VehicleFactory.testVehicle(vehicleId2);
        final Vehicle vehicle3 = VehicleFactory.testVehicle(vehicleId3);
        when(vehicleRepository.vehicles()).thenReturn(asList(vehicle3, vehicle1, vehicle2));
        when(vehicleRepository.removeVehicle(vehicleId1)).thenReturn(vehicle1);

        vehicleService.removeAnyVehicle();

        verify(vehicleRepository).removeVehicle(vehicleId1);
        verify(optimizer).removeVehicle(vehicle1);
    }

    @Test
    void removeAll() {
        vehicleService.removeAll();
        verify(optimizer).removeAllVehicles();
        verify(vehicleRepository).removeAll();
    }

    @Test
    void changeCapacity() {
        final long vehicleId = 1;
        final int capacity = 123;
        final Vehicle originalVehicle = VehicleFactory.createVehicle(vehicleId, "1", capacity - 10);
        when(vehicleRepository.find(vehicleId)).thenReturn(Optional.of(originalVehicle));

        vehicleService.changeCapacity(vehicleId, capacity);

        verify(vehicleRepository).update(vehicleArgumentCaptor.capture());
        assertThat(vehicleArgumentCaptor.getValue().id()).isEqualTo(vehicleId);
        assertThat(vehicleArgumentCaptor.getValue().capacity()).isEqualTo(capacity);

        verify(optimizer).changeCapacity(vehicleArgumentCaptor.capture());
        assertThat(vehicleArgumentCaptor.getValue().capacity()).isEqualTo(capacity);
    }
}
