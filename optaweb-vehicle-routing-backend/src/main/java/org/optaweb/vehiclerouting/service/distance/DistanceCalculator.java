package org.optaweb.vehiclerouting.service.distance;

import org.optaweb.vehiclerouting.domain.Coordinates;

/**
 * Calculates distances between coordinates.
 */
public interface DistanceCalculator {

    /**
     * Calculate travel time in milliseconds.
     *
     * @param from origin
     * @param to destination
     * @return travel time in milliseconds
     * @throws RoutingException when the distance between given coordinates cannot be calculated
     */
    long travelTimeMillis(Coordinates from, Coordinates to);
}
