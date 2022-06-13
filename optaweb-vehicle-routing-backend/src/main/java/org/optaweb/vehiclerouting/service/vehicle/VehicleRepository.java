package org.optaweb.vehiclerouting.service.vehicle;

import java.util.List;
import java.util.Optional;

import org.optaweb.vehiclerouting.domain.Vehicle;
import org.optaweb.vehiclerouting.domain.VehicleData;

/**
 * Defines repository operations on vehicles.
 */
public interface VehicleRepository {

    /**
     * Create a vehicle with a unique ID.
     *
     * @param capacity vehicle's capacity
     * @return a new vehicle
     */
    Vehicle createVehicle(int capacity);

    /**
     * Create a vehicle from the given data.
     *
     * @param vehicleData vehicle data
     * @return a new vehicle
     */
    Vehicle createVehicle(VehicleData vehicleData);

    /**
     * Get all vehicles.
     *
     * @return all vehicles
     */
    List<Vehicle> vehicles();

    /**
     * Remove a vehicle with the given ID.
     *
     * @param id vehicle's ID
     * @return the removed vehicle
     */
    Vehicle removeVehicle(long id);

    /**
     * Remove all vehicles from the repository.
     */
    void removeAll();

    /**
     * Find a vehicle by its ID.
     *
     * @param vehicleId vehicle's ID
     * @return an Optional containing vehicle with the given ID or empty Optional if there is no vehicle with such ID
     */
    Optional<Vehicle> find(long vehicleId);

    Vehicle changeCapacity(long vehicleId, int capacity);
}
