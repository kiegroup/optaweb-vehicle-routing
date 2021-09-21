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

import javax.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.Distance;
import org.optaweb.vehiclerouting.domain.Location;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class DistanceRepositoryIntegrationTest {

    @Inject
    DistanceCrudRepository crudRepository;

    private DistanceRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new DistanceRepositoryImpl(crudRepository);
    }

    @Test
    @TestTransaction
    void panache_repository_should_persist_and_delete_distances() {
        DistanceKey key = new DistanceKey(1, 2);
        DistanceEntity entity = new DistanceEntity(key, 730107L);

        crudRepository.persist(entity);

        assertThat(crudRepository.count()).isOne();
        assertThat(crudRepository.findById(key)).isEqualTo(entity);

        crudRepository.deleteById(key);
        assertThat(crudRepository.count()).isZero();
    }

    static DistanceEntity distance(long fromId, long toId) {
        return new DistanceEntity(new DistanceKey(fromId, toId), 1L);
    }

    @Test
    @TestTransaction
    void delete_by_fromId_or_toId() {
        DistanceEntity distance23 = distance(2, 3);
        DistanceEntity distance32 = distance(3, 2);

        crudRepository.persist(distance(1, 2));
        crudRepository.persist(distance(2, 1));
        crudRepository.persist(distance23);
        crudRepository.persist(distance32);
        crudRepository.persist(distance(3, 1));
        crudRepository.persist(distance(1, 3));

        assertThat(crudRepository.count()).isEqualTo(6);
        crudRepository.deleteByFromIdOrToId(1L);
        assertThat(crudRepository.count()).isEqualTo(2);
        assertThat(crudRepository.findAll().stream()).containsExactly(distance23, distance32);
    }

    @Test
    @TestTransaction
    void should_return_saved_distance() {
        Location location1 = new Location(1, Coordinates.of(7, -4.0));
        Location location2 = new Location(2, Coordinates.of(5, 9.0));

        Distance distance = Distance.ofMillis(956766417);
        repository.saveDistance(location1, location2, distance);
        assertThat(repository.getDistance(location1, location2)).contains(distance);
    }

    @Test
    void should_return_negative_number_when_distance_not_found() {
        Location location1 = new Location(1, Coordinates.of(7, -4.0));
        Location location2 = new Location(2, Coordinates.of(5, 9.0));

        assertThat(repository.getDistance(location1, location2)).isEmpty();
    }
}
