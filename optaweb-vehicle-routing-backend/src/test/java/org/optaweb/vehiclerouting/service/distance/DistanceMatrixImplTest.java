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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.Distance;
import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.service.location.DistanceMatrixRow;

@ExtendWith(MockitoExtension.class)
class DistanceMatrixImplTest {

    @Mock
    private DistanceCalculator distanceCalculator;
    @InjectMocks
    private DistanceMatrixImpl distanceMatrix;

    @Test
    void should_calculate_distance_map() {
        DistanceMatrixImpl distanceMatrix = new DistanceMatrixImpl(new MockDistanceCalculator());

        Location l0 = location(100, 0);
        Location l1 = location(111, 1);
        Location l9neg = location(321, -9);

        DistanceMatrixRow matrixRow0 = distanceMatrix.addLocation(l0);

        // distance to self
        assertThat(matrixRow0.distanceTo(l0.id())).isEqualTo(Distance.ZERO);
        assertThat(distanceMatrix.distance(l0, l0)).isEqualTo(Distance.ZERO);
        // distance to not yet registered location
        assertThatIllegalArgumentException().isThrownBy(() -> matrixRow0.distanceTo(l1.id()));
        assertThatIllegalArgumentException().isThrownBy(() -> distanceMatrix.distance(l0, l1));
        assertThatIllegalArgumentException().isThrownBy(() -> distanceMatrix.distance(l1, l0));
        assertThatIllegalArgumentException().isThrownBy(() -> distanceMatrix.distance(l1, l1));

        DistanceMatrixRow matrixRow1 = distanceMatrix.addLocation(l1);
        // distance to self
        assertThat(matrixRow1.distanceTo(l1.id())).isEqualTo(Distance.ZERO);
        assertThat(distanceMatrix.distance(l1, l1)).isEqualTo(Distance.ZERO);

        // distance 0 <-> 1
        assertThat(matrixRow1.distanceTo(l0.id())).isEqualTo(Distance.ofMillis(1));
        assertThat(distanceMatrix.distance(l0, l1)).isEqualTo(Distance.ofMillis(1));
        assertThat(matrixRow0.distanceTo(l1.id())).isEqualTo(Distance.ofMillis(1));
        assertThat(distanceMatrix.distance(l1, l0)).isEqualTo(Distance.ofMillis(1));

        DistanceMatrixRow matrixRow9 = distanceMatrix.addLocation(l9neg);

        // distances -9 -> {0, 1}
        assertThat(matrixRow9.distanceTo(l0.id())).isEqualTo(Distance.ofMillis(9));
        assertThat(distanceMatrix.distance(l9neg, l0)).isEqualTo(Distance.ofMillis(9));
        assertThat(matrixRow9.distanceTo(l1.id())).isEqualTo(Distance.ofMillis(10));
        assertThat(distanceMatrix.distance(l9neg, l1)).isEqualTo(Distance.ofMillis(10));
        // distances {0, 1} -> -9
        assertThat(matrixRow0.distanceTo(l9neg.id())).isEqualTo(Distance.ofMillis(9));
        assertThat(distanceMatrix.distance(l0, l9neg)).isEqualTo(Distance.ofMillis(9));
        assertThat(matrixRow1.distanceTo(l9neg.id())).isEqualTo(Distance.ofMillis(10));
        assertThat(distanceMatrix.distance(l1, l9neg)).isEqualTo(Distance.ofMillis(10));

        // clear the map
        assertThat(distanceMatrix.dimension()).isEqualTo(3);
        distanceMatrix.clear();
        assertThat(distanceMatrix.dimension()).isZero();
        Location l500 = location(500, 500);
        DistanceMatrixRow matrixRow500 = distanceMatrix.addLocation(l500);
        assertThatIllegalArgumentException().isThrownBy(() -> matrixRow500.distanceTo(l0.id()));
        assertThatIllegalArgumentException().isThrownBy(() -> distanceMatrix.distance(l500, l0));
        assertThatIllegalArgumentException().isThrownBy(() -> matrixRow9.distanceTo(l500.id()));
        assertThatIllegalArgumentException().isThrownBy(() -> distanceMatrix.distance(l9neg, l500));
    }

    @Test
    void should_calculate_distance_only_once() {
        Location l1 = location(100, -1);
        Location l2 = location(111, 20);
        long dist12 = 12;
        long dist21 = 21;
        when(distanceCalculator.travelTimeMillis(l1.coordinates(), l2.coordinates())).thenReturn(dist12);
        when(distanceCalculator.travelTimeMillis(l2.coordinates(), l1.coordinates())).thenReturn(dist21);

        // No calculation for the first location.
        distanceMatrix.addLocation(l1);
        verifyNoInteractions(distanceCalculator);

        // Calculation happens for the first time.
        distanceMatrix.addLocation(l2);
        verify(distanceCalculator).travelTimeMillis(l1.coordinates(), l2.coordinates());
        verify(distanceCalculator).travelTimeMillis(l2.coordinates(), l1.coordinates());

        // No calculation if the matrix is already populated.
        DistanceMatrixRow row21 = distanceMatrix.addLocation(l2);
        assertThat(row21.distanceTo(l1.id())).isEqualTo(Distance.ofMillis(dist21));

        DistanceMatrixRow row12 = distanceMatrix.addLocation(l1);
        assertThat(row12.distanceTo(l2.id())).isEqualTo(Distance.ofMillis(dist12));

        verifyNoMoreInteractions(distanceCalculator);
    }

    @Test
    void should_remove_distance_row_from_matrix_and_repository_when_location_removed() {
        // arrange
        Location l1 = location(1, 1);
        Location l2 = location(2, 2);
        when(distanceCalculator.travelTimeMillis(l1.coordinates(), l2.coordinates()))
                .thenThrow(new DistanceCalculationException("dummy"));

        distanceMatrix.addLocation(l1);
        assertThatExceptionOfType(DistanceCalculationException.class).isThrownBy(() -> distanceMatrix.addLocation(l2));
        assertThat(distanceMatrix.dimension()).isEqualTo(1);

        // act & assert
        distanceMatrix.removeLocation(l1);
        assertThat(distanceMatrix.dimension()).isZero();

        distanceMatrix.addLocation(l2);
        assertThat(distanceMatrix.dimension()).isEqualTo(1);
    }

    @Test
    void get_distance_after_put() {
        Location from = location(1, 1);
        Location to = location(2, 2);
        Distance distance = Distance.ofMillis(2000);
        distanceMatrix.put(from, to, distance);
        assertThat(distanceMatrix.distance(from, to)).isEqualTo(distance);
        verifyNoInteractions(distanceCalculator);
    }

    private static Location location(long id, int longitude) {
        return new Location(id, new Coordinates(BigDecimal.ZERO, BigDecimal.valueOf(longitude)));
    }

    private static class MockDistanceCalculator implements DistanceCalculator {

        @Override
        public long travelTimeMillis(Coordinates from, Coordinates to) {
            // imagine 1D space (all locations on equator)
            return (long) Math.abs(to.longitude().doubleValue() - from.longitude().doubleValue());
        }
    }
}
