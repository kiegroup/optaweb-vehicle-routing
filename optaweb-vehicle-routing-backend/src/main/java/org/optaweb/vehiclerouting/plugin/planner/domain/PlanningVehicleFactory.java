package org.optaweb.vehiclerouting.plugin.planner.domain;

import org.optaweb.vehiclerouting.domain.Vehicle;

/**
 * Creates {@link PlanningVehicle} instances.
 */
public class PlanningVehicleFactory {

    private PlanningVehicleFactory() {
        throw new AssertionError("Utility class");
    }

    /**
     * Create planning vehicle from domain vehicle.
     *
     * @param domainVehicle domain vehicle
     * @return planning vehicle
     */
    public static PlanningVehicle fromDomain(Vehicle domainVehicle) {
        return vehicle(domainVehicle.id(), domainVehicle.capacity());
    }

    /**
     * Create a testing vehicle with zero capacity.
     *
     * @param id vehicle's ID
     * @return new vehicle with zero capacity
     */
    public static PlanningVehicle testVehicle(long id) {
        return vehicle(id, 0);
    }

    /**
     * Create a testing vehicle with capacity.
     *
     * @param id vehicle's ID
     * @return new vehicle with the given capacity
     */
    public static PlanningVehicle testVehicle(long id, int capacity) {
        return vehicle(id, capacity);
    }

    private static PlanningVehicle vehicle(long id, int capacity) {
        PlanningVehicle vehicle = new PlanningVehicle();
        vehicle.setId(id);
        vehicle.setCapacity(capacity);
        return vehicle;
    }
}
