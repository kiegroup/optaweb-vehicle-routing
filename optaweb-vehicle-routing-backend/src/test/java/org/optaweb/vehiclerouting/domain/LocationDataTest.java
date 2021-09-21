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

package org.optaweb.vehiclerouting.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class LocationDataTest {

    @Test
    void constructor_params_must_not_be_null() {
        assertThatNullPointerException().isThrownBy(() -> new LocationData(null, ""));
        assertThatNullPointerException().isThrownBy(() -> new LocationData(Coordinates.of(1, 1), null));
    }

    @Test
    void locations_are_equal_if_they_have_same_properties() {
        Coordinates coordinates0 = new Coordinates(BigDecimal.ZERO, BigDecimal.ZERO);
        Coordinates coordinates1 = new Coordinates(BigDecimal.ONE, BigDecimal.ONE);
        String description = "test description";
        LocationData equalLocationData = new LocationData(coordinates0, description);

        final LocationData locationData = new LocationData(coordinates0, description);

        assertThat(locationData)
                // different coordinates
                .isNotEqualTo(new LocationData(coordinates1, description))
                // different description
                .isNotEqualTo(new LocationData(coordinates0, "xyz"))
                // null
                .isNotEqualTo(null)
                // different type with equal properties
                .isNotEqualTo(new Location(0, coordinates0, description))
                // same object -> OK
                .isEqualTo(locationData)
                // same properties -> OK
                .isEqualTo(equalLocationData)
                // equal objects => same hashCode
                .hasSameHashCodeAs(equalLocationData);
    }
}
