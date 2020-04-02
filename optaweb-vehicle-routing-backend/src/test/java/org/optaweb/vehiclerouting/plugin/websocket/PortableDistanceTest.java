/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import org.optaweb.vehiclerouting.domain.Distance;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class PortableDistanceTest {

    private JacksonTester<PortableDistance> json;

    @BeforeEach
    void setUp() {
        // This initializes the json field
        JacksonTester.initFields(this, new ObjectMapper());
    }

    @Test
    void marshal_to_json() throws IOException {
        Distance distance = Distance.ofMillis(3_661_987);
        assertThat(json.write(PortableDistance.fromDistance(distance)).getJson()).isEqualTo("\"1h 1m 2s\"");
    }

    @Test
    void from_distance() {
        assertThatNullPointerException().isThrownBy(() -> PortableDistance.fromDistance(null));
    }

    @Test
    void equals_hashCode_toString() {
        long millis = 173_000;
        Distance distance = Distance.ofMillis(millis);
        PortableDistance portableDistance = PortableDistance.fromDistance(distance);

        // equals()
        assertThat(portableDistance).isEqualTo(portableDistance);
        assertThat(portableDistance).isEqualTo(PortableDistance.fromDistance(distance));

        assertThat(portableDistance).isNotEqualTo(null);
        assertThat(portableDistance).isNotEqualTo(millis);
        assertThat(portableDistance).isNotEqualTo(PortableDistance.fromDistance(Distance.ofMillis(millis - 501)));

        // hashCode()
        assertThat(portableDistance).hasSameHashCodeAs(PortableDistance.fromDistance(distance));

        // toString()
        assertThat(portableDistance.toString()).contains("0h 2m 53s");
    }
}
