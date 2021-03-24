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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.optaweb.vehiclerouting.domain.Distance;
import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.service.location.DistanceMatrix;
import org.optaweb.vehiclerouting.service.location.DistanceMatrixRow;

@ApplicationScoped
class DistanceMatrixImpl implements DistanceMatrix {

    private final DistanceCalculator distanceCalculator;
    private final Map<Location, Map<Long, Distance>> matrix = new HashMap<>();

    @Inject
    DistanceMatrixImpl(DistanceCalculator distanceCalculator) {
        this.distanceCalculator = distanceCalculator;
    }

    @Override
    public DistanceMatrixRow addLocation(Location newLocation) {
        Map<Long, Distance> distancesToOthers = updateMatrixLazily(newLocation);
        return locationId -> distancesToOthers.computeIfAbsent(locationId, wrongId -> {
            throw new IllegalArgumentException(
                    "Distance from " + newLocation
                            + " to " + wrongId
                            + " hasn't been recorded.\n"
                            + "We only know distances to " + distancesToOthers.keySet());
        });
    }

    private Map<Long, Distance> updateMatrixLazily(Location location) {
        // Matrix == distance rows.
        // We're adding a whole new row with distances from the new location to existing ones.
        // We're also creating a new column by "appending" a new cell to each existing row.
        // This new column contains distances from each existing location to the new one.

        return matrix.computeIfAbsent(location, newLocation -> {
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
                distancesFromOther.put(newLocation.id(), calculateDistance(other, newLocation));
                // Add a cell to the new distance's row.
                distancesToOthers.put(other.id(), calculateDistance(newLocation, other));
            });

            return distancesToOthers;
        });
    }

    private Distance calculateDistance(Location from, Location to) {
        return Distance.ofMillis(distanceCalculator.travelTimeMillis(from.coordinates(), to.coordinates()));
    }

    @Override
    public Distance distance(Location from, Location to) {
        if (!matrix.containsKey(from)) {
            throw new IllegalArgumentException("Unknown 'from' location (" + from + ")");
        }
        Map<Long, Distance> distanceRow = matrix.get(from);
        if (!distanceRow.containsKey(to.id())) {
            throw new IllegalArgumentException("Unknown 'to' location (" + to + ")");
        }
        return distanceRow.get(to.id());
    }

    @Override
    public void put(Location from, Location to, Distance distance) {
        matrix.computeIfAbsent(from, location -> new ConcurrentHashMap<>()).put(to.id(), distance);
    }

    @Override
    public void removeLocation(Location location) {
        // Remove the distance matrix row (distances from the removed location to others).
        matrix.remove(location);
        // TODO also remove the "column" of the matrix (distances from others to the removed location) to avoid memory
        //  leak.
        //  But this probably requires making DistanceMatrixRow immutable (otherwise there's a risk of NPEs in solver)
        //  and update PlanningLocations' distance maps through problem fact changes.
    }

    @Override
    public void clear() {
        matrix.clear();
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
