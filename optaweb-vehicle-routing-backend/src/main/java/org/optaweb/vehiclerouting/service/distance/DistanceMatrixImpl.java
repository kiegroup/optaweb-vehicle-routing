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

package org.optaweb.vehiclerouting.service.distance;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.optaweb.vehiclerouting.domain.LocationNew;
import org.optaweb.vehiclerouting.service.location.DistanceMatrix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
class DistanceMatrixImpl implements DistanceMatrix {

    private final DistanceCalculator distanceCalculator;
    private final DistanceRepository distanceRepository;
    private final Map<LocationNew, Map<Long, Double>> matrix = new HashMap<>();

    @Autowired
    DistanceMatrixImpl(DistanceCalculator distanceCalculator, DistanceRepository distanceRepository) {
        this.distanceCalculator = distanceCalculator;
        this.distanceRepository = distanceRepository;
    }

    @Override
    public void addLocation(LocationNew newLocationNew) {
        // Matrix == distance rows.
        // We're adding a whole new row with distances from the new location to existing ones.
        // We're also creating a new column by "appending" a new cell to each existing row.
        // This new column contains distances from each existing location to the new one.

        // The map must be thread-safe because:
        // - we're updating it from the parallel stream below
        // - it is accessed from org.optaweb.vehiclerouting.solver thread!
        Map<Long, Double> distancesToOthers = new ConcurrentHashMap<>(); // the new row

        // distance to self is 0
        distancesToOthers.put(newLocationNew.id(), 0.0);

        // for all entries (rows) in the matrix:
        matrix.entrySet().stream().parallel().forEach(distanceRow -> {
            // entry key is the existing (other) location
            LocationNew other = distanceRow.getKey();
            // entry value is the data (cells) in the row (distances from the entry key location to any other)
            Map<Long, Double> distancesFromOther = distanceRow.getValue();
            // add a new cell to the row with the distance from the entry key location to the new location
            // (results in a new column at the end of the loop)
            distancesFromOther.put(newLocationNew.id(), calculateOrRestoreDistance(other, newLocationNew));
            // add a cell the new distance's row
            distancesToOthers.put(other.id(), calculateOrRestoreDistance(newLocationNew, other));
        });

        matrix.put(newLocationNew, distancesToOthers);
    }

    private double calculateOrRestoreDistance(LocationNew from, LocationNew to) {
        double distance = distanceRepository.getDistance(from, to);
        if (distance < 0) {
            distance = distanceCalculator.travelTimeMillis(from.coordinates(), to.coordinates());
            distanceRepository.saveDistance(from, to, distance);
        }
        return distance;
    }

    @Override
    public Map<Long, Double> getRow(LocationNew locationNew) {
        return matrix.get(locationNew);
    }

    @Override
    public void clear() {
        matrix.clear();
    }
}
