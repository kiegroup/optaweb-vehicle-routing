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

package org.optaweb.vehiclerouting.plugin.websocket;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.optaweb.vehiclerouting.domain.VehicleFactory;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class PortableVehicleTest {

    private JacksonTester<PortableVehicle> json;

    @BeforeEach
    void setUp() {
        JacksonTester.initFields(this, new ObjectMapper());
    }

    @Test
    void marshall_to_json() throws IOException {
        long id = 321;
        String name = "Pink: {XY-123} \"B\"";
        int capacity = 78;
        PortableVehicle portableVehicle = new PortableVehicle(id, name, capacity);
        String jsonTemplate = "{\"id\":%d,\"name\":\"%s\",\"capacity\":%d}";
        assertThat(json.write(portableVehicle)).isEqualToJson(
                String.format(jsonTemplate, id, name.replaceAll("\"", "\\\\\""), capacity)
        );
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

        // equals()
        assertThat(portableVehicle).isNotEqualTo(null);
        assertThat(portableVehicle).isNotEqualTo(VehicleFactory.createVehicle(id, name, capacity));
        assertThat(portableVehicle).isNotEqualTo(new PortableVehicle(id + 1, name, capacity));
        assertThat(portableVehicle).isNotEqualTo(new PortableVehicle(id, name + "z", capacity));
        assertThat(portableVehicle).isNotEqualTo(new PortableVehicle(id, name, capacity + 1));
        assertThat(portableVehicle).isEqualTo(portableVehicle);
        assertThat(portableVehicle).isEqualTo(new PortableVehicle(id, name, capacity));

        // hasCode()
        assertThat(portableVehicle).hasSameHashCodeAs(new PortableVehicle(id, name, capacity));

        // toString()
        assertThat(portableVehicle.toString()).contains(
                String.valueOf(id),
                name,
                String.valueOf(capacity)
        );
    }
}
