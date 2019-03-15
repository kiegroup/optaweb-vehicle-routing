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

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RouteTest {

    private final Location depot = new Location(1, LatLng.valueOf(5, 5));
    private final Location visit1 = new Location(2, LatLng.valueOf(5, 5));
    private final Location visit2 = new Location(3, LatLng.valueOf(5, 5));

    @Test
    public void constructor_args_not_null() {
        assertThatThrownBy(() -> new Route(depot, null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new Route(null, Collections.emptyList())).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void visits_should_not_contain_depot() {
        assertThatThrownBy(() -> new Route(depot, Arrays.asList(depot, visit1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(depot.toString());
        assertThatThrownBy(() -> new Route(depot, Arrays.asList(visit1, depot, visit2)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(depot.toString());
    }

    @Test
    public void no_customer_should_be_visited_twice_by_the_same_vehicle() {
        assertThatThrownBy(() -> new Route(depot, Arrays.asList(visit1, visit1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("(1)");
    }

    @Test
    public void cannot_modify_visits_externally() {
        ArrayList<Location> visits = new ArrayList<>();
        visits.add(visit1);
        Route route = new Route(depot, visits);

        assertThatThrownBy(() -> route.visits().clear()).isInstanceOf(UnsupportedOperationException.class);
    }
}
