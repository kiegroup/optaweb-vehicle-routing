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

package org.optaweb.vehiclerouting.plugin.routing;

import java.util.HashMap;
import java.util.Map;

import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.interactor.location.DistanceMatrix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Keeps the information about distances between every pair of locations.
 */
@Component
public class DistanceMatrixImpl implements DistanceMatrix {

    private final RouterImpl router;
    private final Map<Location, Map<Long, Double>> matrix = new HashMap<>();

    @Autowired
    public DistanceMatrixImpl(RouterImpl router) {
        this.router = router;
    }

    @Override
    public synchronized void addLocation(Location location) {
        Map<Long, Double> distanceMap = new HashMap<>();
        distanceMap.put(location.getId(), 0.0);
        for (Map.Entry<Location, Map<Long, Double>> entry : matrix.entrySet()) {
            Location other = entry.getKey();
            distanceMap.put(other.getId(), router.getDistance(location.getLatLng(), other.getLatLng()));
            entry.getValue().put(location.getId(), router.getDistance(other.getLatLng(), location.getLatLng()));
        }
        matrix.put(location, distanceMap);
    }

    @Override
    public synchronized Map<Long, Double> getRow(Location location) {
        return matrix.get(location);
    }
}
