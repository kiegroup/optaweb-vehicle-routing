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

package org.optaweb.vehiclerouting.service.route;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;
import org.optaweb.vehiclerouting.domain.LatLng;
import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.domain.Route;
import org.optaweb.vehiclerouting.domain.RouteWithTrack;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RoutingPlanTest {

    private final Location depot = new Location(1, LatLng.valueOf(5, 5));
    private final RouteWithTrack emptyRoute = new RouteWithTrack(new Route(depot, emptyList()), emptyList());

    @Test
    public void constructor_args_not_null() {
        assertThatThrownBy(() -> new RoutingPlan(null, depot, emptyList())).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new RoutingPlan("", depot, null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void no_routes_without_a_depot() {
        assertThatThrownBy(() -> new RoutingPlan("", null, Arrays.asList(emptyRoute)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void cannot_modify_routes_externally() {
        ArrayList<RouteWithTrack> routes = new ArrayList<>();
        routes.add(emptyRoute);
        RoutingPlan routingPlan = new RoutingPlan("", depot, routes);

        assertThatThrownBy(() -> routingPlan.routes().clear()).isInstanceOf(UnsupportedOperationException.class);
    }
}
