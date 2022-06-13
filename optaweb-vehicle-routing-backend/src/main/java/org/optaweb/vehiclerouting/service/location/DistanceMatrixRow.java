package org.optaweb.vehiclerouting.service.location;

import org.optaweb.vehiclerouting.domain.Distance;

/**
 * Contains {@link Distance distances} from the location associated with this row to other locations.
 */
public interface DistanceMatrixRow {

    /**
     * Distance from this row's location to the given location.
     *
     * @param locationId target location
     * @return time it takes to travel to the given location
     */
    Distance distanceTo(long locationId);
}
