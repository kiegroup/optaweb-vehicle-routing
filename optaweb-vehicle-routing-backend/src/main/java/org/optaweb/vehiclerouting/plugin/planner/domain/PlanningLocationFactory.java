package org.optaweb.vehiclerouting.plugin.planner.domain;

import org.optaweb.vehiclerouting.domain.Location;

/**
 * Creates {@link PlanningLocation}s.
 */
public class PlanningLocationFactory {

    private PlanningLocationFactory() {
        throw new AssertionError("Utility class");
    }

    /**
     * Create planning location without a distance map. This location cannot be used for planning but can be used for
     * a problem fact change to remove a visit.
     *
     * @param location domain location
     * @return planning location without distance map
     */
    public static PlanningLocation fromDomain(Location location) {
        return fromDomain(location, PlanningLocationFactory::failFast);
    }

    /**
     * Create planning location from a domain location and a distance map.
     *
     * @param location domain location
     * @param distanceMap distance map of this planning location
     * @return planning location
     */
    public static PlanningLocation fromDomain(Location location, DistanceMap distanceMap) {
        return new PlanningLocation(
                location.id(),
                location.coordinates().latitude().doubleValue(),
                location.coordinates().longitude().doubleValue(),
                distanceMap);
    }

    /**
     * Create test location without distance map and coordinates. Coordinates will be initialized to zero.
     *
     * @param id location ID
     * @return planning location without distance map and coordinates
     */
    public static PlanningLocation testLocation(long id) {
        return testLocation(id, PlanningLocationFactory::failFast);
    }

    /**
     * Create test location with distance map and without coordinates. Coordinates will be initialized to zero.
     *
     * @param id location ID
     * @param distanceMap distance map
     * @return planning location with distance map and without coordinates
     */
    public static PlanningLocation testLocation(long id, DistanceMap distanceMap) {
        return new PlanningLocation(id, 0, 0, distanceMap);
    }

    private static long failFast(PlanningLocation location) {
        throw new IllegalStateException();
    }
}
