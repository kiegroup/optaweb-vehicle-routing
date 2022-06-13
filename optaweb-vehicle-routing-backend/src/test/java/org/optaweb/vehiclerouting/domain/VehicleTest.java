package org.optaweb.vehiclerouting.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import org.junit.jupiter.api.Test;

class VehicleTest {

    @Test
    void constructor_params_must_not_be_null() {
        assertThatNullPointerException().isThrownBy(() -> new Vehicle(0, null, 0));
    }

    @Test
    void vehicles_are_identified_based_on_id() {
        final long id = 0;
        final String description = "test description";
        final int capacity = 1;
        final Vehicle vehicle = new Vehicle(id, description, capacity);

        assertThat(vehicle)
                // different ID
                .isNotEqualTo(new Vehicle(id + 1, description, capacity))
                // null
                .isNotEqualTo(null)
                // different class
                .isNotEqualTo(id)
                // same object -> OK
                .isEqualTo(vehicle)
                // same properties -> OK
                .isEqualTo(new Vehicle(id, description, capacity))
                // same ID, different description -> OK
                .isEqualTo(new Vehicle(id, description + "x", capacity))
                // same ID, different capacity -> OK
                .isEqualTo(new Vehicle(id, description, capacity + 1));
    }

    @Test
    void equal_vehicles_must_have_same_hashcode() {
        long id = 1;
        assertThat(new Vehicle(id, "description 1", 1))
                .hasSameHashCodeAs(new Vehicle(id, "description 2", 2));
    }
}
