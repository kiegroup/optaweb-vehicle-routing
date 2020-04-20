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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VehicleRepositoryImplTest {

    @Mock
    private VehicleCrudRepository crudRepository;
    @InjectMocks
    private VehicleRepositoryImpl repository;
    @Captor
    private ArgumentCaptor<VehicleEntity> vehicleEntityCaptor;

    private Vehicle testVehicle() {
        long id = 19;
        String name = "vehicle name";
        int capacity = 1100;
        return VehicleFactory.createVehicle(id, name, capacity);
    }

    VehicleEntity mockVehicleEntity(Vehicle vehicle) {
        VehicleEntity vehicleEntity = mock(VehicleEntity.class);
        when(vehicleEntity.getId()).thenReturn(vehicle.id());
        when(vehicleEntity.getName()).thenReturn(vehicle.name());
        when(vehicleEntity.getCapacity()).thenReturn(vehicle.capacity());
        return vehicleEntity;
    }

    @Test
    void should_create_vehicle_and_generate_id_and_name() {
        // arrange
        VehicleEntity savedEntity = mockVehicleEntity(testVehicle());
        when(crudRepository.save(vehicleEntityCaptor.capture())).thenReturn(savedEntity);
        int savedCapacity = 1;

        // act
        Vehicle createdVehicle = repository.createVehicle(savedCapacity);

        // assert
        // -- the correct values were used to save the entity
        List<VehicleEntity> savedVehicles = vehicleEntityCaptor.getAllValues();
        assertThat(savedVehicles).hasSize(2);

        assertThat(savedVehicles.get(0).getName()).isNull();
        assertThat(savedVehicles.get(0).getCapacity()).isEqualTo(savedCapacity);
        assertThat(savedVehicles.get(1).getName()).isEqualTo("Vehicle " + savedEntity.getId());
        assertThat(savedVehicles.get(1).getCapacity()).isEqualTo(savedCapacity);

        // -- created domain vehicle is equal to the entity returned by repository.save()
        // This may be confusing but that's the contract of Spring Repository API.
        // The entity instance that is being saved is meant to be discarded. The returned instance should be used
        // for further operations as the save() operation may update it (for example generate the ID).
        assertThat(createdVehicle.id()).isEqualTo(savedEntity.getId());
        assertThat(createdVehicle.name()).isEqualTo(savedEntity.getName());
        assertThat(createdVehicle.capacity()).isEqualTo(savedEntity.getCapacity());
    }

    @Test
    void create_vehicle_from_given_data() {
        // arrange
        VehicleEntity savedEntity = mockVehicleEntity(testVehicle());
        when(crudRepository.save(vehicleEntityCaptor.capture())).thenReturn(savedEntity);
        int savedCapacity = 1;

        VehicleData vehicleData = VehicleFactory.vehicleData("x", 1);

        // act
        Vehicle createdVehicle = repository.createVehicle(vehicleData);

        // assert
        // -- the correct values were used to save the entity
        VehicleEntity savedVehicle = vehicleEntityCaptor.getValue();

        assertThat(savedVehicle.getName()).isEqualTo(vehicleData.name());
        assertThat(savedVehicle.getCapacity()).isEqualTo(vehicleData.capacity());

        // -- created domain vehicle is equal to the entity returned by repository.save()
        assertThat(createdVehicle.id()).isEqualTo(savedEntity.getId());
        assertThat(createdVehicle.name()).isEqualTo(savedEntity.getName());
        assertThat(createdVehicle.capacity()).isEqualTo(savedEntity.getCapacity());
    }

    @Test
    void remove_created_location_by_id() {
        Vehicle testVehicle = testVehicle();
        VehicleEntity locationEntity = mockVehicleEntity(testVehicle);
        final long id = testVehicle.id();
        when(crudRepository.findById(id)).thenReturn(Optional.of(locationEntity));

        Vehicle removed = repository.removeVehicle(id);
        assertThat(removed).isEqualTo(testVehicle);
        verify(crudRepository).deleteById(id);
    }

    @Test
    void removing_nonexistent_vehicle_should_fail() {
        when(crudRepository.findById(anyLong())).thenReturn(Optional.empty());

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
        Vehicle testVehicle = testVehicle();
        VehicleEntity locationEntity = mockVehicleEntity(testVehicle);
        when(crudRepository.findAll()).thenReturn(Collections.singletonList(locationEntity));
        assertThat(repository.vehicles()).containsExactly(testVehicle);
    }

    @Test
    void find_by_id() {
        Vehicle testVehicle = testVehicle();
        VehicleEntity vehicleEntity = mockVehicleEntity(testVehicle);
        when(crudRepository.findById(testVehicle.id())).thenReturn(Optional.of(vehicleEntity));
        assertThat(repository.find(testVehicle.id())).contains(testVehicle);
    }

    @Test
    void update() {
        Vehicle testVehicle = testVehicle();

        repository.update(testVehicle);

        verify(crudRepository).save(vehicleEntityCaptor.capture());

        VehicleEntity savedVehicle = vehicleEntityCaptor.getValue();
        assertThat(savedVehicle.getId()).isEqualTo(testVehicle.id());
        assertThat(savedVehicle.getName()).isEqualTo(testVehicle.name());
        assertThat(savedVehicle.getCapacity()).isEqualTo(testVehicle.capacity());
    }
}
