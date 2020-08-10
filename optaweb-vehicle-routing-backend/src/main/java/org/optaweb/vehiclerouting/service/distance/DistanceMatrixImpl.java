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

import org.optaweb.vehiclerouting.domain.Distance;
import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.service.location.DistanceMatrix;
import org.optaweb.vehiclerouting.service.location.DistanceMatrixRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
class DistanceMatrixImpl implements DistanceMatrix {

    private final DistanceCalculator distanceCalculator;
    private final DistanceRepository distanceRepository;
    private final Map<Location, Map<Long, Distance>> matrix = new HashMap<>();

    @Autowired
    DistanceMatrixImpl(DistanceCalculator distanceCalculator, DistanceRepository distanceRepository) {
        this.distanceCalculator = distanceCalculator;
        this.distanceRepository = distanceRepository;
    }

    @Override
    public DistanceMatrixRow addLocation(Location newLocation) {
        // Matrix == distance rows.
        // We're adding a whole new row with distances from the new location to existing ones.
        // We're also creating a new column by "appending" a new cell to each existing row.
        // This new column contains distances from each existing location to the new one.

        // The map must be thread-safe because:
        // - we're updating it from the parallel stream below
        // - it is accessed from solver thread!
        Map<Long, Distance> distancesToOthers = new ConcurrentHashMap<>(); // the new row

        // distance to self is 0
        distancesToOthers.put(newLocation.id(), Distance.ZERO);

        // For all entries (rows) in the matrix:
        matrix.entrySet().stream().parallel().forEach(distanceRow -> {
            // Entry key is the existing (other) location.
            Location other = distanceRow.getKey();
            // Entry value is the data (cells) in the row (distances from the entry key location to any other).
            Map<Long, Distance> distancesFromOther = distanceRow.getValue();
            // Add a new cell to the row with the distance from the entry key location to the new location
            // (results in a new column at the end of the loop).
            distancesFromOther.put(newLocation.id(), calculateOrRestoreDistance(other, newLocation));
            // Add a cell to the new distance's row.
            distancesToOthers.put(other.id(), calculateOrRestoreDistance(newLocation, other));
        });

        matrix.put(newLocation, distancesToOthers);

        return locationId -> {
            if (!distancesToOthers.containsKey(locationId)) {
                throw new IllegalArgumentException(
                        "Distance from " + newLocation
                                + " to " + locationId
                                + " hasn't been recorded.\n"
                                + "We only know distances to " + distancesToOthers.keySet());
            }
            return distancesToOthers.get(locationId);
        };
    }

    private Distance calculateOrRestoreDistance(Location from, Location to) {
        long distance = distanceRepository.getDistance(from, to);
        if (distance < 0) {
            distance = distanceCalculator.travelTimeMillis(from.coordinates(), to.coordinates());
            distanceRepository.saveDistance(from, to, distance);
        }
        return Distance.ofMillis(distance);
    }

    @Override
    public void removeLocation(Location location) {
        // Remove the distance matrix row (distances from the removed location to others).
        matrix.remove(location);
        // TODO also remove the "column" of the matrix (distances from others to the removed location) to avoid memory
        //  leak.
        //  But this probably requires making DistanceMatrixRow immutable (otherwise there's a risk of NPEs in solver)
        //  and update PlanningLocations' distance maps through problem fact changes.
        distanceRepository.deleteDistances(location);
    }

    @Override
    public void clear() {
        matrix.clear();
        distanceRepository.deleteAll();
    }

    /**
     * Number of rows in the matrix.
     *
     * @return number of rows
     */
    public int dimension() {
        return matrix.size();
    }
}
