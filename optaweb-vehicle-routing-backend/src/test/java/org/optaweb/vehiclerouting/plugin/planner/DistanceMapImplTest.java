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

import java.util.HashMap;

import org.junit.jupiter.api.Test;
import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningLocation;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningLocationFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class DistanceMapImplTest {

    @Test
    void distance_map_should_return_distance_associated_with_a_location() {
        Location location1 = new Location(1, Coordinates.valueOf(1, 1));
        long otherId = 2;
        PlanningLocation location2 = PlanningLocationFactory.testLocation(otherId);
        long distance = 45000;
        HashMap<Long, Long> hashMap = new HashMap<>(1);
        hashMap.put(otherId, distance);
        DistanceMapImpl distanceMap = new DistanceMapImpl(location1, hashMap);
        assertThat(distanceMap.distanceTo(location2)).isEqualTo(distance);
    }

    @Test
    void should_throw_illegal_argument_exception() {
        Location location1 = new Location(1, Coordinates.valueOf(1, 1));
        PlanningLocation location2 = PlanningLocationFactory.testLocation(2);
        DistanceMapImpl distanceMap = new DistanceMapImpl(location1, new HashMap<>());
        assertThatIllegalArgumentException().isThrownBy(() -> distanceMap.distanceTo(location2));
    }
}
