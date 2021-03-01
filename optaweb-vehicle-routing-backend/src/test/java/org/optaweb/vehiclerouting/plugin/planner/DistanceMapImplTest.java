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

package org.optaweb.vehiclerouting.plugin.planner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import org.junit.jupiter.api.Test;
import org.optaweb.vehiclerouting.domain.Distance;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningLocation;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningLocationFactory;
import org.optaweb.vehiclerouting.service.location.DistanceMatrixRow;

class DistanceMapImplTest {

    @Test
    void matrix_row_must_not_be_null() {
        assertThatNullPointerException().isThrownBy(() -> new DistanceMapImpl(null));
    }

    @Test
    void distance_map_should_return_value_from_distance_matrix_row() {
        PlanningLocation location2 = PlanningLocationFactory.testLocation(2);
        Distance distance = Distance.ofMillis(45000);
        DistanceMatrixRow matrixRow = locationId -> distance;
        DistanceMapImpl distanceMap = new DistanceMapImpl(matrixRow);
        assertThat(distanceMap.distanceTo(location2)).isEqualTo(distance.millis());
    }
}
