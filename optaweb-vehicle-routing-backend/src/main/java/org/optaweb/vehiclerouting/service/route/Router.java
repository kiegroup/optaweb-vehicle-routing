package org.optaweb.vehiclerouting.service.route;

import java.util.List;

import org.optaweb.vehiclerouting.domain.Coordinates;

/**
 * Provides paths between locations.
 */
public interface Router {

    /**
     * Get path between two locations.
     *
     * @param from starting location
     * @param to destination
     * @return list of coordinates describing the path between given locations.
     */
    List<Coordinates> getPath(Coordinates from, Coordinates to);
}
