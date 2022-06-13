package org.optaweb.vehiclerouting.service.vehicle;

import static java.util.Comparator.comparingLong;

import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.optaweb.vehiclerouting.domain.Vehicle;
import org.optaweb.vehiclerouting.domain.VehicleData;

@ApplicationScoped
public class VehicleService {

    static final int DEFAULT_VEHICLE_CAPACITY = 10;

    private final VehiclePlanner planner;
    private final VehicleRepository vehicleRepository;

    @Inject
    public VehicleService(VehiclePlanner planner, VehicleRepository vehicleRepository) {
        this.planner = planner;
        this.vehicleRepository = vehicleRepository;
    }

    @Transactional
    public Vehicle createVehicle() {
        Vehicle vehicle = vehicleRepository.createVehicle(DEFAULT_VEHICLE_CAPACITY);
        addVehicle(vehicle);
        return vehicle;
    }

    @Transactional
    public Vehicle createVehicle(VehicleData vehicleData) {
        Vehicle vehicle = vehicleRepository.createVehicle(vehicleData);
        addVehicle(vehicle);
        return vehicle;
    }

    public void addVehicle(Vehicle vehicle) {
        planner.addVehicle(Objects.requireNonNull(vehicle));
    }

    @Transactional
    public void removeVehicle(long vehicleId) {
        Vehicle vehicle = vehicleRepository.removeVehicle(vehicleId);
        planner.removeVehicle(vehicle);
    }

    public synchronized void removeAnyVehicle() {
        Optional<Vehicle> first = vehicleRepository.vehicles().stream().min(comparingLong(Vehicle::id));
        first.map(Vehicle::id).ifPresent(this::removeVehicle);
    }

    @Transactional
    public void removeAll() {
        planner.removeAllVehicles();
        vehicleRepository.removeAll();
    }

    @Transactional
    public void changeCapacity(long vehicleId, int capacity) {
        Vehicle updatedVehicle = vehicleRepository.changeCapacity(vehicleId, capacity);
        planner.changeCapacity(updatedVehicle);
    }
}
