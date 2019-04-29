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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.optaweb.vehiclerouting.domain.LatLng;
import org.optaweb.vehiclerouting.domain.Location;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LocationRepositoryImplTest {

    @Mock
    private LocationCrudRepository crudRepository;
    @InjectMocks
    private LocationRepositoryImpl repository;
    @Mock
    private LocationEntity locationEntity;
    private Location testLocation;

    @Before
    public void setUp() {
        final long id = 76;
        final BigDecimal latitude = BigDecimal.valueOf(1.2);
        final BigDecimal longitude = BigDecimal.valueOf(3.4);
        final String description = "description";
        testLocation = new Location(id, new LatLng(latitude, longitude), description);
        when(locationEntity.getId()).thenReturn(id);
        when(locationEntity.getLatitude()).thenReturn(latitude);
        when(locationEntity.getLongitude()).thenReturn(longitude);
        when(locationEntity.getDescription()).thenReturn(description);
    }

    @Test
    public void should_create_location_and_generate_id() {
        when(crudRepository.save(any(LocationEntity.class))).thenReturn(locationEntity);

        LatLng latLng = LatLng.valueOf(0.00213, 32.777);
        String description = "a, b & c";
        Location createdLocation = repository.createLocation(latLng, description);
        assertThat(createdLocation.getId()).isEqualTo(testLocation.getId());
        assertThat(createdLocation.getLatLng()).isEqualTo(latLng);
        assertThat(createdLocation.getDescription()).isEqualTo(description);
    }

    @Test
    public void remove_created_location_by_id() {
        final long id = testLocation.getId();
        when(crudRepository.findById(id)).thenReturn(Optional.of(locationEntity));

        Location removed = repository.removeLocation(id);
        assertThat(removed).isEqualTo(testLocation);
        verify(crudRepository).deleteById(id);
    }

    @Test
    public void removing_nonexistent_location_should_fail() {
        when(crudRepository.findById(anyLong())).thenReturn(Optional.empty());

        // removing nonexistent location should fail and its ID should appear in the exception message
        int uniqueNonexistentId = 7173;
        assertThatIllegalArgumentException()
                .isThrownBy(() -> repository.removeLocation(uniqueNonexistentId))
                .withMessageContaining(String.valueOf(uniqueNonexistentId));
    }

    @Test
    public void remove_all_locations() {
        repository.removeAll();
        verify(crudRepository).deleteAll();
    }

    @Test
    public void get_all_locations() {
        when(crudRepository.findAll()).thenReturn(Collections.singletonList(locationEntity));
        assertThat(repository.locations()).containsExactly(testLocation);
    }
}
