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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ExtendWith(SpringExtension.class)
class DistanceRepositoryIntegrationTest {

    @Autowired
    private DistanceCrudRepository crudRepository;
    private DistanceRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new DistanceRepositoryImpl(crudRepository);
    }

    @Test
    void crudRepository() {
        DistanceKey key = new DistanceKey(1, 2);
        DistanceEntity entity = new DistanceEntity(key, 73.0107);

        DistanceEntity savedEntity = crudRepository.save(entity);
        assertThat(savedEntity).isEqualTo(entity);

        assertThat(crudRepository.count()).isOne();
        assertThat(crudRepository.findById(key)).get().isEqualTo(entity);

        crudRepository.deleteById(key);
        assertThat(crudRepository.count()).isZero();
    }

    @Test
    void should_return_saved_distance() {
        Location location1 = new Location(1, Coordinates.valueOf(7, -4.0));
        Location location2 = new Location(2, Coordinates.valueOf(5, 9.0));

        double distance = 95676.6417;
        repository.saveDistance(location1, location2, distance);
        assertThat(repository.getDistance(location1, location2)).isEqualTo(distance);
    }

    @Test
    void should_return_negative_number_when_distance_not_found() {
        Location location1 = new Location(1, Coordinates.valueOf(7, -4.0));
        Location location2 = new Location(2, Coordinates.valueOf(5, 9.0));

        assertThat(repository.getDistance(location1, location2)).isNegative();
    }
}
