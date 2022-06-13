package org.optaweb.vehiclerouting.plugin.planner.domain;

/**
 * Contains travel distances from a reference location to other locations.
 */
@FunctionalInterface
public interface DistanceMap {

    /**
     * Get distance from a reference location to the given location. The actual physical quantity (distance or time)
     * and its units depend on the configuration of the routing engine and is not important for optimization.
     *
     * @param location location the distance of which will be returned
     * @return location's distance
     */
    long distanceTo(PlanningLocation location);
}
