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

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.util.jackson.JacksonAssertions;
import org.optaweb.vehiclerouting.util.junit.FileContent;

class PortableLocationTest {

    private final PortableLocation portableLocation = new PortableLocation(
            987,
            BigDecimal.ONE,
            BigDecimal.TEN,
            "Some Location");

    @Test
    void marshal_to_json(@FileContent("portable-location.json") String json) {
        JacksonAssertions.assertThat(portableLocation).serializedIsEqualToJson(json);
    }

    @Test
    void unmarshal_from_json(@FileContent("portable-location.json") String json) {
        JacksonAssertions.assertThat(json).deserializedIsEqualTo(portableLocation);
    }

    @Test
    void constructor_params_must_not_be_null() {
        assertThatNullPointerException().isThrownBy(
                () -> new PortableLocation(1, null, BigDecimal.ZERO, ""));
        assertThatNullPointerException().isThrownBy(
                () -> new PortableLocation(1, BigDecimal.ZERO, null, ""));
        assertThatNullPointerException().isThrownBy(
                () -> new PortableLocation(1, BigDecimal.ZERO, BigDecimal.ZERO, null));
    }

    @Test
    void fromLocation() {
        Location location = new Location(17, Coordinates.of(5.1, -0.0007), "Hello, world!");
        PortableLocation portableLocation = PortableLocation.fromLocation(location);
        assertThat(portableLocation.getId()).isEqualTo(location.id());
        assertThat(portableLocation.getLatitude()).isEqualTo(location.coordinates().latitude());
        assertThat(portableLocation.getLongitude()).isEqualTo(location.coordinates().longitude());
        assertThat(portableLocation.getDescription()).isEqualTo(location.description());

        assertThatNullPointerException()
                .isThrownBy(() -> PortableLocation.fromLocation(null))
                .withMessageContaining("location");
    }

    @Test
    void equals_hashCode_toString() {
        long id = 123456;
        String description = "x y";
        BigDecimal lat1 = BigDecimal.valueOf(10.0101);
        BigDecimal lat2 = BigDecimal.valueOf(20.2323);
        BigDecimal lon1 = BigDecimal.valueOf(-8.7);
        BigDecimal lon2 = BigDecimal.valueOf(-7.8);
        PortableLocation portableLocation = new PortableLocation(id, lat1, lon1, description);

        assertThat(portableLocation)
                // equals()
                .isNotEqualTo(null)
                .isNotEqualTo(new Location(id, new Coordinates(lat1, lon1)))
                .isNotEqualTo(new PortableLocation(id + 1, lat1, lon1, description))
                .isNotEqualTo(new PortableLocation(id, lat1, lon2, description))
                .isNotEqualTo(new PortableLocation(id, lat2, lon1, description))
                .isNotEqualTo(new PortableLocation(id, lat1, lon1, "y x"))
                .isEqualTo(portableLocation)
                .isEqualTo(new PortableLocation(id, lat1, lon1, description))
                // hasCode()
                .hasSameHashCodeAs(new PortableLocation(id, lat1, lon1, description))
                // toString()
                .asString()
                .contains(
                        String.valueOf(id),
                        lat1.toPlainString(),
                        lon1.toPlainString(),
                        description);
    }
}
