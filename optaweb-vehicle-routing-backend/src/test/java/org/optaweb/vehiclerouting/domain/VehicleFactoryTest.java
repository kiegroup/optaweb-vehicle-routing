package org.optaweb.vehiclerouting.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class VehicleFactoryTest {

    @Test
    void createVehicle() {
        long vehicleId = 4;
        String name = "Vehicle four";
        int capacity = 99;

        Vehicle vehicle = VehicleFactory.createVehicle(vehicleId, name, capacity);

        assertThat(vehicle.id()).isEqualTo(vehicleId);
        assertThat(vehicle.name()).isEqualTo(name);
        assertThat(vehicle.capacity()).isEqualTo(capacity);
    }

    @Test
    void vehicleData() {
        String name = "vehicle name";
        int capacity = 1000;

        VehicleData vehicleData = VehicleFactory.vehicleData(name, capacity);
        assertThat(vehicleData.name()).isEqualTo(name);
        assertThat(vehicleData.capacity()).isEqualTo(capacity);
    }
}
