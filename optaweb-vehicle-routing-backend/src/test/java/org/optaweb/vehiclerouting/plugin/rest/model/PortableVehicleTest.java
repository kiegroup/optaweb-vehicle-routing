package org.optaweb.vehiclerouting.plugin.rest.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import org.junit.jupiter.api.Test;
import org.optaweb.vehiclerouting.domain.VehicleFactory;
import org.optaweb.vehiclerouting.util.jackson.JacksonAssertions;

class PortableVehicleTest {

    @Test
    void marshall_to_json() {
        long id = 321;
        String name = "Pink: {XY-123} \"B\"";
        int capacity = 78;
        PortableVehicle portableVehicle = new PortableVehicle(id, name, capacity);
        String jsonTemplate = "{\"id\":%d,\"name\":\"%s\",\"capacity\":%d}";
        String expected = String.format(jsonTemplate, id, name.replaceAll("\"", "\\\\\""), capacity);
        JacksonAssertions.assertThat(portableVehicle).serializedIsEqualToJson(expected);
    }

    @Test
    void constructor_params_must_not_be_null() {
        assertThatNullPointerException().isThrownBy(() -> new PortableVehicle(1, null, 2));
    }

    @Test
    void fromVehicle() {
        long id = 321;
        String name = "Pink XY-123 B";
        int capacity = 31;
        PortableVehicle portableVehicle = PortableVehicle.fromVehicle(VehicleFactory.createVehicle(id, name, capacity));
        assertThat(portableVehicle.getId()).isEqualTo(id);
        assertThat(portableVehicle.getName()).isEqualTo(name);
        assertThat(portableVehicle.getCapacity()).isEqualTo(capacity);

        assertThatNullPointerException()
                .isThrownBy(() -> PortableVehicle.fromVehicle(null))
                .withMessageContaining("vehicle");
    }

    @Test
    void equals_hashCode_toString() {
        long id = 123456;
        String name = "x y";
        int capacity = 444111;
        PortableVehicle portableVehicle = new PortableVehicle(id, name, capacity);

        assertThat(portableVehicle)
                // equals()
                .isNotEqualTo(null)
                .isNotEqualTo(VehicleFactory.createVehicle(id, name, capacity))
                .isNotEqualTo(new PortableVehicle(id + 1, name, capacity))
                .isNotEqualTo(new PortableVehicle(id, name + "z", capacity))
                .isNotEqualTo(new PortableVehicle(id, name, capacity + 1))
                .isEqualTo(portableVehicle)
                .isEqualTo(new PortableVehicle(id, name, capacity))
                // hasCode()
                .hasSameHashCodeAs(new PortableVehicle(id, name, capacity))
                // toString()
                .asString()
                .contains(
                        String.valueOf(id),
                        name,
                        String.valueOf(capacity));
    }
}
