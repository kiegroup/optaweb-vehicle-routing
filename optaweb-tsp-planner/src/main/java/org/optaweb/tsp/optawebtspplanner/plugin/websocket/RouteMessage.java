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

package org.optaweb.tsp.optawebtspplanner.plugin.websocket;

import java.util.List;

/**
 * Route description.
 */
public class RouteMessage {

    private final String distance;
    private final List<PortableLocation> route;
    private final List<List<PortableLocation>> segments;

    public RouteMessage(String distance, List<PortableLocation> route, List<List<PortableLocation>> segments) {
        this.distance = distance;
        this.route = route;
        this.segments = segments;
    }

    public String getDistance() {
        return distance;
    }

    public List<PortableLocation> getRoute() {
        return route;
    }

    public List<List<PortableLocation>> getSegments() {
        return segments;
    }
}
