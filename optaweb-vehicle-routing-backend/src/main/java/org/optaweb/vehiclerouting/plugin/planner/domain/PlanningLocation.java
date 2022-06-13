package org.optaweb.vehiclerouting.plugin.planner.domain;

import java.util.Objects;

public class PlanningLocation {

    private final long id;
    // Only used to calculate angle.
    private final double latitude;
    private final double longitude;
    private final DistanceMap travelDistanceMap;

    PlanningLocation(long id, double latitude, double longitude, DistanceMap travelDistanceMap) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.travelDistanceMap = Objects.requireNonNull(travelDistanceMap);
    }

    /**
     * ID of the corresponding domain location.
     *
     * @return domain location ID
     */
    public long getId() {
        return id;
    }

    /**
     * Distance to the given location.
     *
     * @param location other location
     * @return distance to the other location
     */
    public long distanceTo(PlanningLocation location) {
        if (this == location) {
            return 0L;
        }
        return travelDistanceMap.distanceTo(location);
    }

    /**
     * Angle between the given location and the direction EAST with {@code this} location being the vertex.
     *
     * @param location location that forms one side of the angle (not {@code null})
     * @return angle in radians in the range of -π to π
     */
    public double angleTo(PlanningLocation location) {
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
