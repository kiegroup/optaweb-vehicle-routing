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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

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

        // different ID
        assertThat(vehicle).isNotEqualTo(new Vehicle(id + 1, description, capacity));
        // null
        assertThat(vehicle).isNotEqualTo(null);
        // different class
        assertThat(vehicle).isNotEqualTo(id);
        // same object -> OK
        assertThat(vehicle).isEqualTo(vehicle);
        // same properties -> OK
        assertThat(vehicle).isEqualTo(new Vehicle(id, description, capacity));
        // same ID, different description -> OK
        assertThat(vehicle).isEqualTo(new Vehicle(id, description + "x", capacity));
        // same ID, different capacity -> OK
        assertThat(vehicle).isEqualTo(new Vehicle(id, description, capacity + 1));
    }
}
