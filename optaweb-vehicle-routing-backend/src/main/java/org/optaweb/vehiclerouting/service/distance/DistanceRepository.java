package org.optaweb.vehiclerouting.service.distance;

import java.util.Optional;

import org.optaweb.vehiclerouting.domain.Distance;
import org.optaweb.vehiclerouting.domain.Location;

/**
 * Stores distances between locations.
 */
public interface DistanceRepository {

    void saveDistance(Location from, Location to, Distance distance);

    Optional<Distance> getDistance(Location from, Location to);

    void deleteDistances(Location location);

    void deleteAll();
}
