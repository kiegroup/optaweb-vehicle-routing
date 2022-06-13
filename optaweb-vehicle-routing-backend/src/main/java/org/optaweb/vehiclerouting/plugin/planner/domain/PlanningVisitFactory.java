package org.optaweb.vehiclerouting.plugin.planner.domain;

/**
 * Creates {@link PlanningVisit} instances.
 */
public class PlanningVisitFactory {

    static final int DEFAULT_VISIT_DEMAND = 1;

    private PlanningVisitFactory() {
        throw new AssertionError("Utility class");
    }

    /**
     * Create visit with {@link #DEFAULT_VISIT_DEMAND}.
     *
     * @param location visit's location
     * @return new visit with the default demand
     */
    public static PlanningVisit fromLocation(PlanningLocation location) {
        return fromLocation(location, DEFAULT_VISIT_DEMAND);
    }

    /**
     * Create visit of a location with the given demand.
     *
     * @param location visit's location
     * @param demand visit's demand
     * @return visit with demand at the given location
     */
    public static PlanningVisit fromLocation(PlanningLocation location, int demand) {
        PlanningVisit visit = new PlanningVisit();
        visit.setId(location.getId());
        visit.setLocation(location);
        visit.setDemand(demand);
        return visit;
    }

    /**
     * Create a test visit with the given ID.
     *
     * @param id ID of the visit and its location
     * @return visit with an ID only
     */
    public static PlanningVisit testVisit(long id) {
        return fromLocation(PlanningLocationFactory.testLocation(id));
    }
}
