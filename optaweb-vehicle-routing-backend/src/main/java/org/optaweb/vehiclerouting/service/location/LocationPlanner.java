package org.optaweb.vehiclerouting.service.location;

import org.optaweb.vehiclerouting.domain.Location;

/**
 * Optimizes the routing plan in response to location-related changes in the routing problem.
 */
public interface LocationPlanner {

    void addLocation(Location location, DistanceMatrixRow distanceMatrixRow);

    void removeLocation(Location location);

    void removeAllLocations();
}
