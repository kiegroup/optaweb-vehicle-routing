/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaweb.vehiclerouting.domain.location.segmented;

import java.util.Map;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.location.Location;

/**
 * Like {@link Location},
 * but for high scale problems to avoid the memory issue of keeping the entire cost matrix in memory.
 */
@XStreamAlias("VrpSegmentLocation")
public class SegmentLocation extends Location {

    // Prefer Map over array or List because customers might be added and removed in real-time planning.
    protected Map<SegmentLocation, Double> nearbyTravelDistanceMap;
    protected Map<SegmentLocation, Double> hubTravelDistanceMap;

    public SegmentLocation(long id, Coordinates coordinates) {
        super(id, coordinates);
    }

    public SegmentLocation(long id, Coordinates coordinates, String description) {
        super(id, coordinates, description);
    }

    public Map<SegmentLocation, Double> getNearbyTravelDistanceMap() {
        return nearbyTravelDistanceMap;
    }

    public void setNearbyTravelDistanceMap(Map<SegmentLocation, Double> nearbyTravelDistanceMap) {
        this.nearbyTravelDistanceMap = nearbyTravelDistanceMap;
    }

    public Map<SegmentLocation, Double> getHubTravelDistanceMap() {
        return hubTravelDistanceMap;
    }

    public void setHubTravelDistanceMap(Map<SegmentLocation, Double> hubTravelDistanceMap) {
        this.hubTravelDistanceMap = hubTravelDistanceMap;
    }

    @Override
    public long getDistanceTo(Location location) {
        Double distance = getDistanceDouble(location);
        // Multiplied by 1000 to avoid floating point arithmetic rounding errors
        return (long) (distance * 1000.0 + 0.5);
    }

    public Double getDistanceDouble(Location location) {
        Double distance = nearbyTravelDistanceMap.get(location);
        if (distance == null) {
            // location isn't nearby
            distance = getShortestDistanceDoubleThroughHubs((SegmentLocation) location);
        }
        return distance;
    }

    protected double getShortestDistanceDoubleThroughHubs(Location location) {
        double shortestDistance = Double.MAX_VALUE;
        for (Map.Entry<SegmentLocation, Double> entry : hubTravelDistanceMap.entrySet()) {
            double distance = entry.getValue();
            distance += entry.getKey().getDistanceDouble(location);
            if (distance < shortestDistance) {
                shortestDistance = distance;
            }
        }
        return shortestDistance;
    }

}
