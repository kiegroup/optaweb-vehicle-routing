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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.Distance;
import org.optaweb.vehiclerouting.domain.Location;

@ExtendWith(MockitoExtension.class)
class DistanceRepositoryImplTest {

    @Mock
    private DistanceCrudRepository crudRepository;
    @InjectMocks
    private DistanceRepositoryImpl repository;
    @Captor
    private ArgumentCaptor<DistanceEntity> distanceEntityArgumentCaptor;

    private final Location from = new Location(1, Coordinates.of(7, -4.0));
    private final Location to = new Location(2, Coordinates.of(5, 9.0));

    @Test
    void should_save_distance() {
        long distance = 956766417;
        repository.saveDistance(from, to, Distance.ofMillis(distance));
        verify(crudRepository).persist(distanceEntityArgumentCaptor.capture());
        DistanceEntity distanceEntity = distanceEntityArgumentCaptor.getValue();
        assertThat(distanceEntity.getDistance()).isEqualTo(distance);
        assertThat(distanceEntity.getKey().getFromId()).isEqualTo(from.id());
        assertThat(distanceEntity.getKey().getToId()).isEqualTo(to.id());
    }

    @Test
    void should_return_distance_when_entity_is_found() {
        DistanceKey distanceKey = new DistanceKey(from.id(), to.id());
        long distance = 10305;
        DistanceEntity distanceEntity = new DistanceEntity(distanceKey, distance);
        when(crudRepository.findByIdOptional(distanceKey)).thenReturn(Optional.of(distanceEntity));
        assertThat(repository.getDistance(from, to)).contains(Distance.ofMillis(distance));
    }

    @Test
    void should_return_negative_number_when_distance_not_found() {
        when(crudRepository.findByIdOptional(any(DistanceKey.class))).thenReturn(Optional.empty());
        assertThat(repository.getDistance(from, to)).isEmpty();
    }

    @Test
    void should_delete_distance_by_location_id() {
        repository.deleteDistances(from);
        verify(crudRepository).deleteByFromIdOrToId(from.id());
    }

    @Test
    void should_delete_all_distances() {
        repository.deleteAll();
        verify(crudRepository).deleteAll();
    }
}
