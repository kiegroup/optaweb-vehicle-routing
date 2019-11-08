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

package org.optaweb.vehiclerouting.plugin.routing;

import org.junit.jupiter.api.Test;
import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.service.region.BoundingBox;

import static org.assertj.core.api.Assertions.assertThat;

class AirDistanceRouterTest {

    @Test
    void travel_time_should_be_distance_divided_by_speed() {
        AirDistanceRouter router = new AirDistanceRouter();
        Coordinates from = Coordinates.valueOf(0, 0);
        Coordinates to = Coordinates.valueOf(3, 4); // √(3² + 4²) = 5
        long travelTimeMillis = router.travelTimeMillis(from, to);
        assertThat(travelTimeMillis).isEqualTo((long) (5
                * AirDistanceRouter.KILOMETERS_PER_DEGREE
                / AirDistanceRouter.TRAVEL_SPEED_KPH
                * AirDistanceRouter.MILLIS_IN_ONE_HOUR));
    }

    @Test
    void bounding_box_is_the_whole_globe() {
        BoundingBox bounds = new AirDistanceRouter().getBounds();
        assertThat(bounds.getSouthWest()).isEqualTo(Coordinates.valueOf(-90, -180));
        assertThat(bounds.getNorthEast()).isEqualTo(Coordinates.valueOf(90, 180));
    }

    @Test
    void path_from_a_to_b_should_be_the_line_ab() {
        AirDistanceRouter router = new AirDistanceRouter();
        Coordinates from = Coordinates.valueOf(0, 0);
        Coordinates to = Coordinates.valueOf(3, 4);
        assertThat(router.getPath(from, to)).containsExactly(from, to);
    }
}
