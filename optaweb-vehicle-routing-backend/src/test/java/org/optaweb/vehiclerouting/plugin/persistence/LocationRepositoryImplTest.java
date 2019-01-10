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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.optaweb.vehiclerouting.domain.LatLng;
import org.optaweb.vehiclerouting.domain.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@RunWith(SpringRunner.class)
public class LocationRepositoryImplTest {

    @Autowired
    private LocationCrudRepository crudRepository;
    private LocationRepositoryImpl repository;

    @Before
    public void setUp() {
        repository = new LocationRepositoryImpl(crudRepository);
    }

    @Test
    public void remove_created_location() {
        LatLng latLng = LatLng.valueOf(0.00213, 32.777);
        assertThat(crudRepository.count()).isZero();
        Location location = repository.createLocation(latLng);
        assertThat(location.getLatLng()).isEqualTo(latLng);
        assertThat(crudRepository.count()).isOne();

        Location removed = repository.removeLocation(location.getId());
        assertThat(removed).isEqualTo(location);

        // removing the same location twice should fail
        assertThatThrownBy(() -> repository.removeLocation(location.getId()))
                .isInstanceOf(IllegalArgumentException.class);

        // removing nonexistent location should fail and its ID should appear in the exception message
        int uniqueNonexistentId = 7173;
        assertThatThrownBy(() -> repository.removeLocation(uniqueNonexistentId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(String.valueOf(uniqueNonexistentId));
    }

    @Test
    public void remove_all_locations() {
        int locationCount = 132;
        for (int i = 0; i < locationCount; i++) {
            repository.createLocation(LatLng.valueOf(1.0, i / 100.0));
        }
        assertThat(crudRepository.count()).isEqualTo(locationCount);
        repository.removeAll();
        assertThat(crudRepository.count()).isZero();
    }
}
