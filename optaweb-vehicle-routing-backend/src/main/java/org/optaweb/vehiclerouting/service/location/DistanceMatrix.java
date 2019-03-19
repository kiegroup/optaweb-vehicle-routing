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

package org.optaweb.vehiclerouting.service.location;

import java.util.Map;

import org.optaweb.vehiclerouting.domain.Location;

/**
 * Holds distances between every pair of locations.
 */
public interface DistanceMatrix {

    void addLocation(Location location);

    // TODO Currently, the API is encumbered by usage of OptaPlanner VRP example code that works with
    // {@code Map<RoadLocation, Double>}.
    // TODO replace with travelTimeMillis(LatLng, LatLng, VehicleType)
    Map<Long, Double> getRow(Location location);

    void clear();
}
