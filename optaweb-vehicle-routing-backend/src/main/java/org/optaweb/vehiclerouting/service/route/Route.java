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

package org.optaweb.vehiclerouting.service.route;

import java.util.Collections;
import java.util.List;

import org.optaweb.vehiclerouting.domain.LatLng;
import org.optaweb.vehiclerouting.domain.Location;

public class Route {

    private final String distance;
    private final List<Location> route;
    private final List<List<LatLng>> paths;

    public Route(String distance, List<Location> route, List<List<LatLng>> paths) {
        this.distance = distance;
        this.route = route;
        this.paths = paths;
    }

    public static Route empty() {
        return new Route("0", Collections.emptyList(), Collections.emptyList());
    }

    public String getDistance() {
        return distance;
    }

    public List<Location> getRoute() {
        return route;
    }

    public List<List<LatLng>> getPaths() {
        return paths;
    }
}
