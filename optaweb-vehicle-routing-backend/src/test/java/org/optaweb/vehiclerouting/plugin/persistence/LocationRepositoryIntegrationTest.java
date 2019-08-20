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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

@DataJpaTest
@ExtendWith(SpringExtension.class)
class LocationRepositoryIntegrationTest {

    @Autowired
    private LocationCrudRepository crudRepository;
    private LocationRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new LocationRepositoryImpl(crudRepository);
    }

    @Test
    void db_schema() {
        // https://wiki.openstreetmap.org/wiki/Node#Structure
        final BigDecimal maxLatitude = new BigDecimal("90.0000000");
        final BigDecimal maxLongitude = new BigDecimal("214.7483647");
        final BigDecimal minLatitude = maxLatitude.negate();
        final BigDecimal minLongitude = maxLongitude.negate();
        final String description = "...";

        LocationEntity minLocation = new LocationEntity(minLatitude, minLongitude, description);
        LocationEntity maxLocation = new LocationEntity(maxLatitude, maxLongitude, description);
        crudRepository.save(minLocation);
        crudRepository.save(maxLocation);

        assertThat(crudRepository.findById(minLocation.getId())).get().isEqualTo(minLocation);
        assertThat(crudRepository.findById(maxLocation.getId())).get().isEqualTo(maxLocation);
    }

    @Test
    void remove_created_location() {
        Coordinates coordinates = Coordinates.valueOf(0.00213, 32.777);
        assertThat(crudRepository.count()).isZero();
        Location location = repository.createLocation(coordinates, "");
        assertThat(location.coordinates()).isEqualTo(coordinates);
        assertThat(crudRepository.count()).isOne();

        Location removed = repository.removeLocation(location.id());
        assertThat(removed).isEqualTo(location);

        // removing the same location twice should fail
        assertThatIllegalArgumentException().isThrownBy(() -> repository.removeLocation(location.id()));

        // removing nonexistent location should fail and its ID should appear in the exception message
        int uniqueNonexistentId = 7173;
        assertThatIllegalArgumentException().isThrownBy(() -> repository.removeLocation(uniqueNonexistentId))
                .withMessageContaining(String.valueOf(uniqueNonexistentId));
    }

    @Test
    void get_and_remove_all_locations() {
        int locationCount = 8;
        for (int i = 0; i < locationCount; i++) {
            repository.createLocation(Coordinates.valueOf(1.0, i / 100.0), "");
        }

        assertThat(crudRepository.count()).isEqualTo(locationCount);

        // get a sample location entity from the repository
        assertThat(crudRepository.existsById((long) locationCount)).isTrue();
        LocationEntity testEntity = crudRepository
                .findById((long) locationCount)
                .orElseThrow(IllegalStateException::new);
        Location testLocation = new Location(
                testEntity.getId(),
                new Coordinates(testEntity.getLatitude(), testEntity.getLongitude())
        );

        assertThat(repository.locations())
                .hasSize(locationCount)
                .contains(testLocation);

        repository.removeAll();
        assertThat(crudRepository.count()).isZero();
    }
}
