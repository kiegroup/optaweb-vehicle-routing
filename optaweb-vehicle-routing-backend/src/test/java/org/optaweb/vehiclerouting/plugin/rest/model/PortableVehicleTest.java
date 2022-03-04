/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
