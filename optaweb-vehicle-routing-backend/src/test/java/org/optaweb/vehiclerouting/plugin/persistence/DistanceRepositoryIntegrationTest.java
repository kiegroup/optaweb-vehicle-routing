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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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
        DistanceEntity entity = new DistanceEntity(key, 730107L);

        DistanceEntity savedEntity = crudRepository.save(entity);
        assertThat(savedEntity).isEqualTo(entity);

        assertThat(crudRepository.count()).isOne();
        assertThat(crudRepository.findById(key)).get().isEqualTo(entity);

        crudRepository.deleteById(key);
        assertThat(crudRepository.count()).isZero();
    }

    static DistanceEntity distance(long fromId, long toId) {
        return new DistanceEntity(new DistanceKey(fromId, toId), 1L);
    }

    @Test
    void delete_by_fromId_or_toId() {
        DistanceEntity distance23 = distance(2, 3);
        DistanceEntity distance32 = distance(3, 2);

        crudRepository.save(distance(1, 2));
        crudRepository.save(distance(2, 1));
        crudRepository.save(distance23);
        crudRepository.save(distance32);
        crudRepository.save(distance(3, 1));
        crudRepository.save(distance(1, 3));

        assertThat(crudRepository.count()).isEqualTo(6);
        crudRepository.deleteByFromIdOrToId(1L);
        assertThat(crudRepository.count()).isEqualTo(2);
        assertThat(crudRepository.findAll()).containsExactly(distance23, distance32);
    }

    @Test
    void should_return_saved_distance() {
        Location location1 = new Location(1, Coordinates.valueOf(7, -4.0));
        Location location2 = new Location(2, Coordinates.valueOf(5, 9.0));

        long distance = 956766417;
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
