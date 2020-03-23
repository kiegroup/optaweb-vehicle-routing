/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaweb.vehiclerouting.plugin.planner.domain;

import java.util.HashMap;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;
import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.plugin.planner.DistanceMapImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

class PlanningLocationTest {

    @Test
    void distance_to_location_should_equal_value_in_distance_map() {
        HashMap<Long, Long> distanceMap = new HashMap<>();
        long otherId = 321;
        long distance = 777777;
        distanceMap.put(otherId, distance);
        Location domainLocation = new Location(1, Coordinates.valueOf(0, 0));

        PlanningLocation planningLocation = new PlanningLocation(
                domainLocation.id(),
                domainLocation.coordinates().latitude().doubleValue(),
                domainLocation.coordinates().longitude().doubleValue(),
                new DistanceMapImpl(distanceMap::get)
        );
        assertThat(planningLocation.getDistanceTo(PlanningLocationFactory.testLocation(otherId))).isEqualTo(distance);
    }

    @Test
    void angle_from_depot_at_zero_should_be_atan2_of_latitude_longitude() {
        PlanningLocation center = fromCoordinates(0, 0);

        assertThat(center.getAngle(fromCoordinates(0, 1))).isZero();
        assertThat(center.getAngle(fromCoordinates(0, -1))).isEqualTo(Math.PI);
        assertThat(center.getAngle(fromCoordinates(1, 0))).isEqualTo(Math.PI / 2);
        assertThat(center.getAngle(fromCoordinates(-1, 0))).isEqualTo(-Math.PI / 2);
        assertThat(center.getAngle(fromCoordinates(-Double.MIN_VALUE, -1))).isEqualTo(-Math.PI);
        assertThat(center.getAngle(fromCoordinates(-0, 1))).isZero();
    }

    @Test
    void angle_from_depot_on_real_coordinates_should_be_atan2_of_latitude_longitude() {
        PlanningLocation depot = fromCoordinates(1.77, -10.5);
        Offset<Double> offset = offset(0.05);

        assertThat(depot.getAngle(fromCoordinates(1.76, -5))).isCloseTo(0, offset).isNegative();
        assertThat(depot.getAngle(fromCoordinates(100000, -1))).isCloseTo(Math.PI / 2, offset);
    }

    private static PlanningLocation fromCoordinates(double latitude, double longitude) {
        return new PlanningLocation(0, latitude, longitude, location -> 0);
    }
}
