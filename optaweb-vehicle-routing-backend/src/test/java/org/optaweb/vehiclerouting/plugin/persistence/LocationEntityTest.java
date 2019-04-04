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

package org.optaweb.vehiclerouting.plugin.persistence;

import java.math.BigDecimal;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

public class LocationEntityTest {

    @Test
    public void constructor_params_must_not_be_null() {
        assertThatNullPointerException().isThrownBy(() -> new LocationEntity(null, BigDecimal.ZERO));
        assertThatNullPointerException().isThrownBy(() -> new LocationEntity(BigDecimal.ZERO, null));
    }

    @Test
    public void getters() {
        BigDecimal latitude = BigDecimal.valueOf(0.101);
        BigDecimal longitude = BigDecimal.valueOf(101.0);
        LocationEntity locationEntity = new LocationEntity(latitude, longitude);
        assertThat(locationEntity.getId()).isZero();
        assertThat(locationEntity.getLongitude()).isEqualTo(longitude);
        assertThat(locationEntity.getLatitude()).isEqualTo(latitude);
    }
}
