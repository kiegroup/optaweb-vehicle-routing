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

package org.optaweb.vehiclerouting.plugin.persistence.integration_tests;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.optaweb.vehiclerouting.domain.LatLng;
import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.plugin.persistence.LocationCrudRepository;
import org.optaweb.vehiclerouting.plugin.persistence.LocationEntity;
import org.optaweb.vehiclerouting.plugin.persistence.LocationRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@RunWith(SpringRunner.class)
public class LocationRepositoryIntegrationTest {

    @Autowired
    private LocationCrudRepository crudRepository;
    private LocationRepositoryImpl repository;

    @Before
    public void setUp() {
        repository = new LocationRepositoryImpl(crudRepository);
    }

    @Test
    public void db_schema() {
        // https://wiki.openstreetmap.org/wiki/Node#Structure
        final BigDecimal maxLatitude = new BigDecimal("90.0000000");
        final BigDecimal maxLongitude = new BigDecimal("214.7483647");
        final BigDecimal minLatitude = maxLatitude.negate();
        final BigDecimal minLongitude = maxLongitude.negate();

        LocationEntity minLocation = new LocationEntity(minLatitude, minLongitude);
        LocationEntity maxLocation = new LocationEntity(maxLatitude, maxLongitude);
        crudRepository.save(minLocation);
        crudRepository.save(maxLocation);

        assertThat(crudRepository.findById(minLocation.getId())).get().isEqualTo(minLocation);
        assertThat(crudRepository.findById(maxLocation.getId())).get().isEqualTo(maxLocation);
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
    public void get_and_remove_all_locations() {
        int locationCount = 8;
        for (int i = 0; i < locationCount; i++) {
            repository.createLocation(LatLng.valueOf(1.0, i / 100.0));
        }

        assertThat(crudRepository.count()).isEqualTo(locationCount);

        // get a sample location entity from the repository
        assertThat(crudRepository.existsById((long) locationCount)).isTrue();
        LocationEntity testEntity = crudRepository
                .findById((long) locationCount)
                .orElseThrow(IllegalStateException::new);
        Location testLocation = new Location(
                testEntity.getId(),
                new LatLng(testEntity.getLatitude(), testEntity.getLongitude())
        );

        assertThat(repository.locations())
                .hasSize(locationCount)
                .contains(testLocation);

        repository.removeAll();
        assertThat(crudRepository.count()).isZero();
    }
}
