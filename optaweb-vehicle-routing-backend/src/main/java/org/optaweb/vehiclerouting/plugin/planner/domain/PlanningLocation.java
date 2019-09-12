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

import java.math.BigDecimal;
import java.util.Map;

import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.plugin.planner.domain.persistable.AbstractPersistable;

/**
 * A unique location significant to the user.
 */
public class PlanningLocation extends AbstractPersistable {

    private BigDecimal latitude;
    private BigDecimal longitude;
    private String description;

    public PlanningLocation() {
    }

    public PlanningLocation(long id, double latitude, double longitude) {
        super(id);
        this.latitude = BigDecimal.valueOf(latitude);
        this.longitude = BigDecimal.valueOf(longitude);
    }

    public PlanningLocation(long id, BigDecimal latitude, BigDecimal longitude) {
        super(id);
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public PlanningLocation(long id, BigDecimal latitude, BigDecimal longitude, String description) {
        super(id);
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
    }

    public PlanningLocation(Location location) {
        if (location.coordinates() != null) {
            latitude = location.coordinates().latitude();
            longitude = location.coordinates().longitude();
        }
        description = location.description();
        id = location.id();
    }

    // Prefer Map over array or List because customers might be added and removed in real-time planning.
    private Map<PlanningLocation, Double> travelDistanceMap;

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<PlanningLocation, Double> getTravelDistanceMap() {
        return travelDistanceMap;
    }

    public void setTravelDistanceMap(Map<PlanningLocation, Double> travelDistanceMap) {
        this.travelDistanceMap = travelDistanceMap;
    }

    /**
     * Get distance to the given location
     * @param location
     * @return
     */
    public long getDistanceTo(PlanningLocation location) {
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
    public double getAngle(PlanningLocation location) {
        // Euclidean distance (Pythagorean theorem) - not correct when the surface is a sphere
        double latitudeDifference = latitude.subtract(latitude).longValue();
        double longitudeDifference = longitude.subtract(longitude).longValue();
        return Math.atan2(latitudeDifference, longitudeDifference);
    }

    @Override
    public String toString() {
        return "PlanningLocation{" +
                "latitude=" + latitude +
                ",longitude=" + longitude +
                ",description='" + description + '\'' +
                ",travelDistanceMap=" + travelDistanceMap +
                ",id=" + id +
                '}';
    }
}
