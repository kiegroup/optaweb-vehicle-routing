/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.optaweb.vehiclerouting.service.distance;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.optaweb.vehiclerouting.domain.LatLng;
import org.optaweb.vehiclerouting.domain.Location;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DistanceMatrixImplTest {

    @Mock
    private DistanceCalculator distanceCalculator;
    @Mock
    private DistanceRepository distanceRepository;
    @InjectMocks
    private DistanceMatrixImpl distanceMatrix;

    @Test
    public void should_calculate_distance_map() {
        when(distanceRepository.getDistance(any(), any())).thenReturn(-1.0); // empty repository
        DistanceMatrixImpl distanceMatrix = new DistanceMatrixImpl(new MockDistanceCalculator(), distanceRepository);

        Location l0 = location(100, 0);
        Location l1 = location(111, 1);
        Location l9neg = location(321, -9);

        distanceMatrix.addLocation(l0);
        Map<Long, Double> mapL0 = distanceMatrix.getRow(l0);
        assertThat(mapL0.size()).isEqualTo(1);

        // distance to self
        assertThat(mapL0.get(l0.getId())).isEqualTo(0.0);
        // distance to not yet registered location
        assertThat(mapL0).doesNotContainKeys(l1.getId());

        distanceMatrix.addLocation(l1);
        Map<Long, Double> mapL1 = distanceMatrix.getRow(l1);
        // distance to self
        assertThat(mapL1.get(l1.getId())).isEqualTo(0.0);

        // distance 0 <-> 1
        assertThat(mapL1.get(l0.getId())).isEqualTo(-1.0);
        assertThat(mapL0.get(l1.getId())).isEqualTo(1.0);

        distanceMatrix.addLocation(l9neg);
        Map<Long, Double> mapL9 = distanceMatrix.getRow(l9neg);

        // distances -9 -> {0, 1}
        assertThat(mapL9.get(l0.getId())).isEqualTo(9.0);
        assertThat(mapL9.get(l1.getId())).isEqualTo(10.0);
        // distances {0, 1} -> -9
        assertThat(mapL0.get(l9neg.getId())).isEqualTo(-9.0);
        assertThat(mapL1.get(l9neg.getId())).isEqualTo(-10.0);

        // distance map sizes
        assertThat(mapL0.size()).isEqualTo(3);
        assertThat(mapL1.size()).isEqualTo(3);
        assertThat(mapL9.size()).isEqualTo(3);

        // clear the map
        distanceMatrix.clear();
        assertThat(distanceMatrix.getRow(l0)).isNull();
        assertThat(distanceMatrix.getRow(l1)).isNull();
        assertThat(distanceMatrix.getRow(l9neg)).isNull();
    }

    @Test
    public void should_call_router_and_persist_distances_when_repo_is_empty() {
        Location l1 = location(100, -1);
        Location l2 = location(111, 20);
        long dist12 = 12;
        long dist21 = 21;
        when(distanceRepository.getDistance(any(), any())).thenReturn(-1.0);
        when(distanceCalculator.travelTimeMillis(l1.getLatLng(), l2.getLatLng())).thenReturn(dist12);
        when(distanceCalculator.travelTimeMillis(l2.getLatLng(), l1.getLatLng())).thenReturn(dist21);

        // no calculation for the first location
        distanceMatrix.addLocation(l1);
        verifyZeroInteractions(distanceCalculator);
        verifyZeroInteractions(distanceRepository);

        distanceMatrix.addLocation(l2);

        // getting distances from the repository (unsuccessful)
        verify(distanceRepository).getDistance(l2, l1);
        verify(distanceRepository).getDistance(l1, l2);

        // distances are calculated and persisted
        verify(distanceRepository).saveDistance(l2, l1, dist21);
        verify(distanceRepository).saveDistance(l1, l2, dist12);
    }

    @Test
    public void should_not_call_router_when_repo_is_full() {
        Location l1 = location(1, 0);
        Location l2 = location(2, 0);
        when(distanceRepository.getDistance(l1, l2)).thenReturn(0.0);
        when(distanceRepository.getDistance(l2, l1)).thenReturn(1.0);

        // no calculation for the first location
        distanceMatrix.addLocation(l1);
        verifyZeroInteractions(distanceCalculator);
        verifyZeroInteractions(distanceRepository);

        distanceMatrix.addLocation(l2);

        // get distances from the repository
        verify(distanceRepository).getDistance(l2, l1);
        verify(distanceRepository).getDistance(l1, l2);

        // nothing to persist
        verify(distanceRepository, never()).saveDistance(any(Location.class), any(Location.class), anyDouble());
        // no calculation
        verifyZeroInteractions(distanceCalculator);
    }

    private static Location location(long id, int longitude) {
        return new Location(id, new LatLng(BigDecimal.ZERO, BigDecimal.valueOf(longitude)));
    }

    private static class MockDistanceCalculator implements DistanceCalculator {

        @Override
        public long travelTimeMillis(LatLng from, LatLng to) {
            // imagine 1D space (all locations on equator)
            return (long) (to.getLongitude().doubleValue() - from.getLongitude().doubleValue());
        }
    }
}
