package org.optaweb.vehiclerouting.service.location;

import org.optaweb.vehiclerouting.domain.Distance;
import org.optaweb.vehiclerouting.domain.Location;

/**
 * Holds distances between every pair of locations.
 */
public interface DistanceMatrix {

    DistanceMatrixRow addLocation(Location location);

    void removeLocation(Location location);

    void clear();

    Distance distance(Location from, Location to);

    void put(Location from, Location to, Distance distance);
}
