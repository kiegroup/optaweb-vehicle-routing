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

package org.optaweb.vehiclerouting.plugin.rest.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.optaweb.vehiclerouting.domain.Distance;
import org.springframework.boot.test.json.JacksonTester;

import com.fasterxml.jackson.databind.ObjectMapper;

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

        assertThat(portableDistance)
                // equals()
                .isEqualTo(portableDistance)
                .isEqualTo(PortableDistance.fromDistance(distance))
                .isNotEqualTo(null)
                .isNotEqualTo(millis)
                .isNotEqualTo(PortableDistance.fromDistance(Distance.ofMillis(millis - 501)))
                // hashCode()
                .hasSameHashCodeAs(PortableDistance.fromDistance(distance))
                // toString()
                .asString().contains("0h 2m 53s");
    }
}
