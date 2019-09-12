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

package org.optaweb.vehiclerouting.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class RouteTest {

    private final Vehicle vehicle = VehicleFactory.testVehicle(4);
    private final Location depot = new Location(1, Coordinates.valueOf(5, 5));
    private final Location visit1 = new Location(2, Coordinates.valueOf(5, 5));
    private final Location visit2 = new Location(3, Coordinates.valueOf(5, 5));

    @Test
    void constructor_args_not_null() {
        assertThatNullPointerException().isThrownBy(() -> new Route(null, depot, Collections.emptyList()));
        assertThatNullPointerException().isThrownBy(() -> new Route(vehicle, null, Collections.emptyList()));
        assertThatNullPointerException().isThrownBy(() -> new Route(vehicle, depot, null));
    }

    @Test
    void visits_should_not_contain_depot() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Route(vehicle, depot, Arrays.asList(depot, visit1)))
                .withMessageContaining(depot.toString());
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Route(vehicle, depot, Arrays.asList(visit1, depot, visit2)))
                .withMessageContaining(depot.toString());
    }

    @Test
    void no_visit_should_be_visited_twice_by_the_same_vehicle() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Route(vehicle, depot, Arrays.asList(visit1, visit1)))
                .withMessageContaining("(1)");
    }

    @Test
    void cannot_modify_visits_externally() {
        ArrayList<Location> visits = new ArrayList<>();
        visits.add(visit1);
        Route route = new Route(vehicle, depot, visits);

        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> route.visits().clear());
    }
}
