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

package org.optaweb.vehiclerouting.plugin.websocket;

import java.util.List;
import java.util.Objects;

/**
 * Vehicle route representation convenient for marshalling.
 */
public class PortableRoute {

    private final PortableLocation depot;
    private final List<PortableLocation> visits;
    // TODO flatten
    private final List<List<PortableLatLng>> track;

    public PortableRoute(PortableLocation depot, List<PortableLocation> visits, List<List<PortableLatLng>> track) {
        this.depot = Objects.requireNonNull(depot);
        this.visits = Objects.requireNonNull(visits);
        this.track = Objects.requireNonNull(track);
    }

    public PortableLocation getDepot() {
        return depot;
    }

    public List<PortableLocation> getVisits() {
        return visits;
    }

    public List<List<PortableLatLng>> getTrack() {
        return track;
    }
}
