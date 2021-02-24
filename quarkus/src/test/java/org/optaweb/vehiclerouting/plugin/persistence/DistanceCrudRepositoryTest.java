/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class DistanceCrudRepositoryTest {

    @Inject
    DistanceCrudRepository distanceCrudRepository;

    @Test
    @Transactional
    void panache_repository_should_persist_and_delete_distances() {
        DistanceEntity distanceEntity = new DistanceEntity(new DistanceKey(1, 2), 20L);
        distanceCrudRepository.persist(distanceEntity);
        assertThat(distanceCrudRepository.count()).isEqualTo(1);

        distanceCrudRepository.deleteByFromIdOrToId(2);
        assertThat(distanceCrudRepository.count()).isEqualTo(0);
    }
}
