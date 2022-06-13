package org.optaweb.vehiclerouting.service.vehicle;

import org.optaweb.vehiclerouting.domain.Vehicle;

/**
 * Optimizes the routing plan in response to vehicle-related changes in the routing problem.
 */
public interface VehiclePlanner {

    void addVehicle(Vehicle vehicle);

    void removeVehicle(Vehicle vehicle);

    void removeAllVehicles();

    void changeCapacity(Vehicle vehicle);
}
