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
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Vehicle's itinerary including detailed geographical description of the route.
 */
public class RouteWithTrack extends Route {

    private final List<List<LatLng>> track;

    /**
     * Create a route with track. When route is empty (no visits), track must be empty too and vice-versa
     * (non-empty route must have a non-empty track).
     * @param route vehicle route (not null)
     * @param track track going through all visits (not null)
     */
    public RouteWithTrack(Route route, List<List<LatLng>> track) {
        super(route.depot(), route.visits());
        this.track = new ArrayList<>(Objects.requireNonNull(track));
        if (route.visits().isEmpty() && !track.isEmpty() || !route.visits().isEmpty() && track.isEmpty()) {
            throw new IllegalArgumentException("Route and track must be either both empty or both non-empty");
        }
    }

    /**
     * Vehicle's track that goes from vehicle's depot through all visits and returns to the depot.
     * @return vehicles track (not null)
     */
    public List<List<LatLng>> track() {
        return Collections.unmodifiableList(track);
    }
}
