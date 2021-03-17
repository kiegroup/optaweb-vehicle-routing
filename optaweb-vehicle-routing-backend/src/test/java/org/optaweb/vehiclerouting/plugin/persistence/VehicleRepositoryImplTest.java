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

package org.optaweb.vehiclerouting.plugin.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.optaweb.vehiclerouting.domain.Vehicle;
import org.optaweb.vehiclerouting.domain.VehicleData;
import org.optaweb.vehiclerouting.domain.VehicleFactory;

@ExtendWith(MockitoExtension.class)
class VehicleRepositoryImplTest {

    @Mock
    private VehicleCrudRepository crudRepository;
    @InjectMocks
    private VehicleRepositoryImpl repository;
    @Captor
    private ArgumentCaptor<VehicleEntity> vehicleEntityCaptor;

    private final Vehicle testVehicle = VehicleFactory.createVehicle(19, "vehicle name", 1100);

    private static VehicleEntity vehicleEntity(Vehicle vehicle) {
        return new VehicleEntity(vehicle.id(), vehicle.name(), vehicle.capacity());
    }

    @Test
    void should_create_vehicle() {
        // arrange
        int savedCapacity = 1;

        // act
        Vehicle newVehicle = repository.createVehicle(savedCapacity);

        // assert
        // -- the correct values were used to save the entity
        verify(crudRepository).persist(vehicleEntityCaptor.capture());
        VehicleEntity savedVehicle = vehicleEntityCaptor.getValue();
        assertThat(savedVehicle.getName()).isNotNull();
        assertThat(savedVehicle.getCapacity()).isEqualTo(savedCapacity);

        // -- created domain vehicle has the expected values
        assertThat(newVehicle.name()).isNotNull();
        assertThat(newVehicle.capacity()).isEqualTo(savedCapacity);
    }

    @Test
    void create_vehicle_from_given_data() {
        // arrange
        String name = "x";
        int capacity = 111;
        VehicleData vehicleData = VehicleFactory.vehicleData(name, capacity);

        // act
        Vehicle newVehicle = repository.createVehicle(vehicleData);

        // assert
        assertThat(newVehicle.name()).isEqualTo(name);
        assertThat(newVehicle.capacity()).isEqualTo(capacity);
    }

    @Test
    void remove_created_vehicle_by_id() {
        VehicleEntity vehicleEntity = vehicleEntity(testVehicle);
        final long id = testVehicle.id();
        when(crudRepository.findByIdOptional(id)).thenReturn(Optional.of(vehicleEntity));

        Vehicle removed = repository.removeVehicle(id);
        assertThat(removed).isEqualTo(testVehicle);
        verify(crudRepository).deleteById(id);
    }

    @Test
    void removing_nonexistent_vehicle_should_fail() {
        when(crudRepository.findByIdOptional(anyLong())).thenReturn(Optional.empty());

        // removing nonexistent vehicle should fail and its ID should appear in the exception message
        int uniqueNonexistentId = 7173;
        assertThatIllegalArgumentException()
                .isThrownBy(() -> repository.removeVehicle(uniqueNonexistentId))
                .withMessageContaining(String.valueOf(uniqueNonexistentId));
    }

    @Test
    void remove_all_vehicles() {
        repository.removeAll();
        verify(crudRepository).deleteAll();
    }

    @Test
    void get_all_vehicles() {
        VehicleEntity vehicleEntity = vehicleEntity(testVehicle);
        when(crudRepository.streamAll()).thenReturn(Stream.of(vehicleEntity));
        assertThat(repository.vehicles()).containsExactly(testVehicle);
    }

    @Test
    void find_by_id() {
        VehicleEntity vehicleEntity = vehicleEntity(testVehicle);
        when(crudRepository.findByIdOptional(testVehicle.id())).thenReturn(Optional.of(vehicleEntity));
        assertThat(repository.find(testVehicle.id())).contains(testVehicle);
    }

    @Test
    void update() {
        long vehicleId = 123;
        String name = "xy";
        int capacity = 80;

        VehicleEntity vehicleEntity = new VehicleEntity(vehicleId, name, capacity - 10);
        when(crudRepository.findByIdOptional(vehicleId)).thenReturn(Optional.of(vehicleEntity));

        Vehicle vehicle = repository.changeCapacity(vehicleId, capacity);
        verify(crudRepository).flush();

        assertThat(vehicleEntity.getCapacity()).isEqualTo(capacity);

        assertThat(vehicle.id()).isEqualTo(vehicleId);
        assertThat(vehicle.name()).isEqualTo(name);
        assertThat(vehicle.capacity()).isEqualTo(capacity);
    }
}
