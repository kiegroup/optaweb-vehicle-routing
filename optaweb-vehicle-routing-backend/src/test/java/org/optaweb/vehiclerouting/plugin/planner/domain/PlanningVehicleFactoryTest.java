package org.optaweb.vehiclerouting.plugin.planner.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVehicleFactory.fromDomain;

import org.junit.jupiter.api.Test;
import org.optaweb.vehiclerouting.domain.Vehicle;
import org.optaweb.vehiclerouting.domain.VehicleFactory;

class PlanningVehicleFactoryTest {

    @Test
    void planning_vehicle() {
        long vehicleId = 2;
        String name = "not used";
        int capacity = 7;
        Vehicle domainVehicle = VehicleFactory.createVehicle(vehicleId, name, capacity);

        PlanningVehicle vehicle = fromDomain(domainVehicle);

        assertThat(vehicle.getId()).isEqualTo(vehicleId);
        assertThat(vehicle.getCapacity()).isEqualTo(capacity);
    }
}
