/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class LocationTest {

    @Test
    void constructor_params_must_not_be_null() {
        assertThatNullPointerException().isThrownBy(() -> new Location(0, null, ""));
        assertThatNullPointerException().isThrownBy(() -> new Location(0, Coordinates.valueOf(1, 1), null));
    }

    @Test
    void locations_are_identified_based_on_id() {
        final Coordinates coordinates0 = new Coordinates(BigDecimal.ZERO, BigDecimal.ZERO);
        final Coordinates coordinates1 = new Coordinates(BigDecimal.ONE, BigDecimal.ONE);
        final String description = "test description";
        final long id = 0;

        final Location location = new Location(id, coordinates0, description);

        // different ID
        assertThat(location).isNotEqualTo(new Location(1, coordinates0, description));
        // null
        assertThat(location).isNotEqualTo(null);
        // different class
        assertThat(location).isNotEqualTo(new LocationData(coordinates0, description));
        // same object -> OK
        assertThat(location).isEqualTo(location);
        // same properties -> OK
        assertThat(location).isEqualTo(new Location(id, coordinates0, description));
        // same ID, different coordinate -> OK
        assertThat(location).isEqualTo(new Location(id, coordinates1, description));
        // same ID, different description -> OK
        assertThat(location).isEqualTo(new Location(id, coordinates0, "xyz"));
    }

    @Test
    void constructor_without_description_should_create_empty_description() {
        assertThat(new Location(7, Coordinates.valueOf(3.14, 4.13)).description()).isEmpty();
    }
}
