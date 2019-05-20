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

import java.math.BigDecimal;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

public class LocationDataTest {

    @Test
    public void constructor_params_must_not_be_null() {
        assertThatNullPointerException().isThrownBy(() -> new LocationData(null, ""));
        assertThatNullPointerException().isThrownBy(() -> new LocationData(LatLng.valueOf(1, 1), null));
    }

    @Test
    public void locations_are_equal_if_they_have_same_properties() {
        LatLng latLng0 = new LatLng(BigDecimal.ZERO, BigDecimal.ZERO);
        LatLng latLng1 = new LatLng(BigDecimal.ONE, BigDecimal.ONE);
        String description = "test description";

        final LocationData locationData = new LocationData(latLng0, description);

        // different coordinates
        assertThat(locationData).isNotEqualTo(new LocationData(latLng1, description));
        // different description
        assertThat(locationData).isNotEqualTo(new LocationData(latLng0, "xyz"));
        // null
        assertThat(locationData).isNotEqualTo(null);
        // different type with equal properties
        assertThat(locationData).isNotEqualTo(new Location(0, latLng0, description));
        // same object -> OK
        assertThat(locationData).isEqualTo(locationData);
        // same properties -> OK
        assertThat(locationData).isEqualTo(new LocationData(latLng0, description));
    }
}
