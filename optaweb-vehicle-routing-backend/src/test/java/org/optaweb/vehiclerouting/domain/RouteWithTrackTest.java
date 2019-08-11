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

import org.junit.jupiter.api.Test;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class RouteWithTrackTest {

    private final Vehicle vehicle = VehicleFactory.testVehicle(4);
    private final Location depot = new Location(1, Coordinates.valueOf(5, 5));
    private final Location visit1 = new Location(2, Coordinates.valueOf(5, 5));
    private final Location visit2 = new Location(3, Coordinates.valueOf(5, 5));

    @Test
    void constructor_args_not_null() {
        Route route = new Route(vehicle, depot, emptyList());
        assertThatNullPointerException().isThrownBy(() -> new RouteWithTrack(route, null));
        assertThatNullPointerException().isThrownBy(() -> new RouteWithTrack(null, emptyList()));
    }

    @Test
    void cannot_modify_track_externally() {
        Route route = new Route(vehicle, depot, Arrays.asList(visit1, visit2));
        ArrayList<List<Coordinates>> track = new ArrayList<>();
        track.add(Arrays.asList(Coordinates.valueOf(1.0, 2.0)));

        RouteWithTrack routeWithTrack = new RouteWithTrack(route, track);
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> routeWithTrack.track().clear());
    }

    @Test
    void when_route_is_empty_track_must_be_empty() {
        Route emptyRoute = new Route(vehicle, depot, emptyList());
        ArrayList<List<Coordinates>> track = new ArrayList<>();
        track.add(Arrays.asList(Coordinates.valueOf(1.0, 2.0)));

        assertThatIllegalArgumentException().isThrownBy(() -> new RouteWithTrack(emptyRoute, track));
    }

    @Test
    void when_route_is_nonempty_track_must_be_nonempty() {
        Route route = new Route(vehicle, depot, Arrays.asList(visit1, visit2));
        ArrayList<List<Coordinates>> emptyTrack = new ArrayList<>();

        assertThatIllegalArgumentException().isThrownBy(() -> new RouteWithTrack(route, emptyTrack));
    }
}
