package org.optaweb.vehiclerouting.domain;

/**
 * Creates {@link Vehicle} instances.
 */
public class VehicleFactory {

    private VehicleFactory() {
        throw new AssertionError("Utility class");
    }

    /**
     * Create vehicle data.
     *
     * @param name vehicle's name
     * @param capacity vehicle's capacity
     * @return vehicle data
     */
    public static VehicleData vehicleData(String name, int capacity) {
        return new VehicleData(name, capacity);
    }

    /**
     * Create a new vehicle with the given ID, name and capacity.
     *
     * @param id vehicle's ID
     * @param name vehicle's name
     * @param capacity vehicle's capacity
     * @return new vehicle
     */
    public static Vehicle createVehicle(long id, String name, int capacity) {
        return new Vehicle(id, name, capacity);
    }

    /**
     * Create a vehicle with given ID and capacity of zero. The vehicle will have a non-empty name.
     *
     * @param id vehicle's ID
     * @return new testing vehicle instance
     */
    public static Vehicle testVehicle(long id) {
        return new Vehicle(id, "Vehicle " + id, 0);
    }
}
