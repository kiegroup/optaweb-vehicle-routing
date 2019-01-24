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
import java.util.concurrent.ConcurrentHashMap;

import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.service.location.DistanceMatrix;
import org.optaweb.vehiclerouting.service.route.Router;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Keeps the information about distances between every pair of locations.
 */
@Component
public class DistanceMatrixImpl implements DistanceMatrix {

    private final Router router;
    private final DistanceRepository distanceRepository;
    private final Map<Location, Map<Long, Double>> matrix = new HashMap<>();

    @Autowired
    public DistanceMatrixImpl(Router router, DistanceRepository distanceRepository) {
        this.router = router;
        this.distanceRepository = distanceRepository;
    }

    @Override
    public void addLocation(Location location) {
        // The map must be thread-safe because it is accessed from solver thread!
        Map<Long, Double> distanceMap = new ConcurrentHashMap<>();
        distanceMap.put(location.getId(), 0.0);
        for (Map.Entry<Location, Map<Long, Double>> entry : matrix.entrySet()) {
            Location other = entry.getKey();
            distanceMap.put(other.getId(), calculateOrRestoreDistance(location, other));
            entry.getValue().put(location.getId(), calculateOrRestoreDistance(other, location));
        }
        matrix.put(location, distanceMap);
    }

    private double calculateOrRestoreDistance(Location from, Location to) {
        double distance = distanceRepository.getDistance(from, to);
        if (distance < 0) {
            distance = router.getDistance(from.getLatLng(), to.getLatLng());
            distanceRepository.saveDistance(from, to, distance);
        }
        return distance;
    }

    @Override
    public Map<Long, Double> getRow(Location location) {
        return matrix.get(location);
    }

    @Override
    public void clear() {
        matrix.clear();
    }
}
