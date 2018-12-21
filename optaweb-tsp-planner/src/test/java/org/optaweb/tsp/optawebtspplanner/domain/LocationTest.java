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

package org.optaweb.tsp.optawebtspplanner.domain;

import java.math.BigDecimal;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LocationTest {

    @Test
    public void constructor_params_must_not_be_null() {
        assertThatThrownBy(() -> new Location(0, null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void locations_are_equal_only_if_they_have_same_id_and_coordinate() {
        LatLng latLng0 = new LatLng(BigDecimal.ZERO, BigDecimal.ZERO);
        LatLng latLng1 = new LatLng(BigDecimal.ONE, BigDecimal.ONE);

        final Location location = new Location(0, latLng0);

        // different ID
        assertThat(location).isNotEqualTo(new Location(1, latLng0));
        // different coordinate
        assertThat(location).isNotEqualTo(new Location(0, latLng1));
        // null
        assertThat(location).isNotEqualTo(null);
        // same properties -> OK
        assertThat(location).isEqualTo(new Location(0, latLng0));
    }
}
