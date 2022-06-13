package org.optaweb.vehiclerouting.plugin.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class VehicleEntityTest {

    @Test
    void getters() {
        long id = 321;
        String name = "Vehicle XY";
        int capacity = 11;
        VehicleEntity vehicleEntity = new VehicleEntity(id, name, capacity);
        assertThat(vehicleEntity.getId()).isEqualTo(id);
        assertThat(vehicleEntity.getName()).isEqualTo(name);
        assertThat(vehicleEntity.getCapacity()).isEqualTo(capacity);
    }
}
