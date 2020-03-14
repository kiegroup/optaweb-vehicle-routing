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

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.Location;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocationRepositoryImplTest {

    @Mock
    private LocationCrudRepository crudRepository;
    @InjectMocks
    private LocationRepositoryImpl repository;
    @Captor
    private ArgumentCaptor<LocationEntity> locationEntityCaptor;

    private Location testLocation() {
        final long id = 76;
        final BigDecimal latitude = BigDecimal.valueOf(1.2);
        final BigDecimal longitude = BigDecimal.valueOf(3.4);
        final String description = "description";
        return new Location(id, new Coordinates(latitude, longitude), description);
    }

    LocationEntity mockLocationEntity(Location location) {
        LocationEntity locationEntity = mock(LocationEntity.class);
        when(locationEntity.getId()).thenReturn(location.id());
        when(locationEntity.getLatitude()).thenReturn(location.coordinates().latitude());
        when(locationEntity.getLongitude()).thenReturn(location.coordinates().longitude());
        when(locationEntity.getDescription()).thenReturn(location.description());
        return locationEntity;
    }

    @Test
    void should_create_location_and_generate_id() {
        // arrange
        LocationEntity locationEntity = mockLocationEntity(testLocation());
        when(crudRepository.save(locationEntityCaptor.capture())).thenReturn(locationEntity);
        Coordinates savedCoordinates = Coordinates.valueOf(0.00213, 32.777);
        String savedDescription = "new location";

        // act
        Location createdLocation = repository.createLocation(savedCoordinates, savedDescription);

        // assert
        // -- the correct values were used to save the entity
        LocationEntity savedLocation = locationEntityCaptor.getValue();
        assertThat(savedLocation.getLatitude()).isEqualTo(savedCoordinates.latitude());
        assertThat(savedLocation.getLongitude()).isEqualTo(savedCoordinates.longitude());
        assertThat(savedLocation.getDescription()).isEqualTo(savedDescription);

        // -- created domain location is equal to the entity returned by repository.save()
        // This may be confusing but that's the contract of Spring Repository API.
        // The entity instance that is being saved is meant to be discarded. The returned instance should be used
        // for further operations as the save() operation may update it (for example generate the ID).
        assertThat(createdLocation.id()).isEqualTo(locationEntity.getId());
        assertThat(createdLocation.coordinates())
                .isEqualTo(new Coordinates(locationEntity.getLatitude(), locationEntity.getLongitude()));
        assertThat(createdLocation.description()).isEqualTo(locationEntity.getDescription());
    }

    @Test
    void remove_created_location_by_id() {
        Location testLocation = testLocation();
        LocationEntity locationEntity = mockLocationEntity(testLocation);
        final long id = testLocation.id();
        when(crudRepository.findById(id)).thenReturn(Optional.of(locationEntity));

        Location removed = repository.removeLocation(id);
        assertThat(removed).isEqualTo(testLocation);
        verify(crudRepository).deleteById(id);
    }

    @Test
    void removing_nonexistent_location_should_fail() {
        when(crudRepository.findById(anyLong())).thenReturn(Optional.empty());

        // removing nonexistent location should fail and its ID should appear in the exception message
        int uniqueNonexistentId = 7173;
        assertThatIllegalArgumentException()
                .isThrownBy(() -> repository.removeLocation(uniqueNonexistentId))
                .withMessageContaining(String.valueOf(uniqueNonexistentId));
    }

    @Test
    void remove_all_locations() {
        repository.removeAll();
        verify(crudRepository).deleteAll();
    }

    @Test
    void get_all_locations() {
        Location testLocation = testLocation();
        LocationEntity locationEntity = mockLocationEntity(testLocation);
        when(crudRepository.findAll()).thenReturn(Collections.singletonList(locationEntity));
        assertThat(repository.locations()).containsExactly(testLocation);
    }

    @Test
    void find_by_id() {
        Location testLocation = testLocation();
        LocationEntity locationEntity = mockLocationEntity(testLocation);
        when(crudRepository.findById(testLocation.id())).thenReturn(Optional.of(locationEntity));
        assertThat(repository.find(testLocation.id())).contains(testLocation);
    }
}
