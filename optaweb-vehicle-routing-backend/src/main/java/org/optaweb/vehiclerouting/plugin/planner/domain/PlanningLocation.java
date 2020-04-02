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

package org.optaweb.vehiclerouting.plugin.planner.domain;

import java.util.Objects;

public class PlanningLocation extends AbstractPlanningObject {

    // Only used to calculate angle.
    private final double latitude;
    private final double longitude;
    private final DistanceMap travelDistanceMap;

    PlanningLocation(long id, double latitude, double longitude, DistanceMap travelDistanceMap) {
        super(id);
        this.latitude = latitude;
        this.longitude = longitude;
        this.travelDistanceMap = Objects.requireNonNull(travelDistanceMap);
    }

    /**
     * Get distance to the given location.
     * @param location other location
     * @return distance to the other location
     */
    public long getDistanceTo(PlanningLocation location) {
        if (this == location) {
            return 0L;
        }
        return travelDistanceMap.distanceTo(location);
    }

    /**
     * The angle relative to the direction EAST.
     * @param location never null
     * @return in Cartesian coordinates
     */
    public double getAngle(PlanningLocation location) {
        // Euclidean distance (Pythagorean theorem) - not correct when the surface is a sphere
        double latitudeDifference = location.latitude - latitude;
        double longitudeDifference = location.longitude - longitude;
        return Math.atan2(latitudeDifference, longitudeDifference);
    }

    @Override
    public String toString() {
        return "PlanningLocation{" +
                "latitude=" + latitude +
                ",longitude=" + longitude +
                ",travelDistanceMap=" + travelDistanceMap +
                ",id=" + id +
                '}';
    }
}
