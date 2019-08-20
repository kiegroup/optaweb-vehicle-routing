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

package org.optaweb.vehiclerouting.domain;

import java.util.Map;
import java.util.Objects;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * A unique location significant to the user.
 */
@XStreamAlias("VrpLocation")
public class Location extends LocationData {


    // Prefer Map over array or List because customers might be added and removed in real-time planning.
    private Map<Location, Double> travelDistanceMap;

    public Location(long id, Coordinates coordinates) {
        // TODO remove this?
        this(id, coordinates, "");
    }

    public Location(long id, Coordinates coordinates, String description) {
        super(coordinates, description);
        this.id = id;
    }
    public Location(long id, double latitude, double longitude) {
        this(id, Coordinates.valueOf(latitude,longitude));
    }

    public long id() {
        return id;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Location location = (Location) o;
        return id == location.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }

    @Override
    public String toString() {
        return "Location{" +
                "id=" + id +
                '}';
    }


    public void setTravelDistanceMap(Map<Location, Double> travelDistanceMap) {
        this.travelDistanceMap = travelDistanceMap;
    }

    /**
     * Get distance to the given location
     * @param location
     * @return
     */
    public long getDistanceTo(Location location) {
        if (this == location) {
            return 0L;
        }
        double distance = travelDistanceMap.get(location);
        // Multiplied by 1000 to avoid floating point arithmetic rounding errors
        return (long) (distance * 1000.0 + 0.5);
    }

    /**
     * The angle relative to the direction EAST.
     * @param location never null
     * @return in Cartesian coordinates
     */
    public double getAngle(Location location) {
        // Euclidean distance (Pythagorean theorem) - not correct when the surface is a sphere
        double latitudeDifference = location.coordinates().latitude().subtract(coordinates().latitude()).longValue();
        double longitudeDifference = location.coordinates().longitude().subtract(coordinates().longitude()).longValue();
        return Math.atan2(latitudeDifference, longitudeDifference);
    }
}
