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
import java.util.List;

import org.junit.Test;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RouteWithTrackTest {

    private final Location depot = new Location(1, LatLng.valueOf(5, 5));
    private final Location visit1 = new Location(2, LatLng.valueOf(5, 5));
    private final Location visit2 = new Location(3, LatLng.valueOf(5, 5));

    @Test
    public void constructor_args_not_null() {
        Route route = new Route(depot, emptyList());
        assertThatThrownBy(() -> new RouteWithTrack(route, null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new Route(null, emptyList())).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void cannot_modify_track_externally() {
        Route route = new Route(depot, Arrays.asList(visit1, visit2));
        ArrayList<List<LatLng>> track = new ArrayList<>();
        track.add(Arrays.asList(LatLng.valueOf(1.0, 2.0)));

        RouteWithTrack routeWithTrack = new RouteWithTrack(route, track);
        assertThatThrownBy(() -> routeWithTrack.track().clear()).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void when_route_is_empty_track_must_be_empty() {
        Route emptyRoute = new Route(depot, emptyList());
        ArrayList<List<LatLng>> track = new ArrayList<>();
        track.add(Arrays.asList(LatLng.valueOf(1.0, 2.0)));

        assertThatThrownBy(() -> new RouteWithTrack(emptyRoute, track)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void when_route_is_nonempty_track_must_be_nonempty() {
        Route route = new Route(depot, Arrays.asList(visit1, visit2));
        ArrayList<List<LatLng>> emptyTrack = new ArrayList<>();

        assertThatThrownBy(() -> new RouteWithTrack(route, emptyTrack)).isInstanceOf(IllegalArgumentException.class);
    }
}
