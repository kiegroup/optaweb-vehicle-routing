package org.optaweb.vehiclerouting.service.vehicle;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.optaweb.vehiclerouting.domain.Vehicle;
import org.optaweb.vehiclerouting.domain.VehicleFactory;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class VehicleServiceIntegrationTest {

    @Inject
    VehicleService vehicleService;

    @Test
    void vehicle_service_should_be_transactional() {
        vehicleService.addVehicle(VehicleFactory.testVehicle(1000));
        Vehicle vehicle1 = vehicleService.createVehicle(VehicleFactory.vehicleData("vehicle", 1));
        Vehicle vehicle2 = vehicleService.createVehicle();
        vehicleService.changeCapacity(vehicle2.id(), vehicle2.capacity() + 100);
        vehicleService.createVehicle();
        vehicleService.removeVehicle(vehicle1.id());
        vehicleService.removeAnyVehicle();
        vehicleService.removeAll();
    }
}
