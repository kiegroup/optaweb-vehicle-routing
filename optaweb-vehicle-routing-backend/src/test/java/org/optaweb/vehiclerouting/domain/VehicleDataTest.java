package org.optaweb.vehiclerouting.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import org.junit.jupiter.api.Test;

class VehicleDataTest {

    @Test
    void constructor_params_must_not_be_null() {
        assertThatNullPointerException().isThrownBy(() -> new VehicleData(null, 1));
    }

    @Test
    void vehicles_are_equal_if_they_have_same_properties() {
        String name = "vehicle name";
        int capacity = 20;

        VehicleData vehicleData = new VehicleData(name, capacity);

        assertThat(vehicleData)
                // different name
                .isNotEqualTo(new VehicleData("", capacity))
                // different capacity
                .isNotEqualTo(new VehicleData(name, capacity + 1))
                // null
                .isNotEqualTo(null)
                // different type with equal properties
                .isNotEqualTo(new Vehicle(0, name, capacity))
                // same object -> OK
                .isEqualTo(vehicleData)
                // same properties -> OK
                .isEqualTo(new VehicleData(name, capacity));
    }
}
