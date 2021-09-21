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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class CoordinatesTest {

    @Test
    void constructor_params_must_not_be_null() {
        assertThatNullPointerException().isThrownBy(() -> new Coordinates(null, BigDecimal.ZERO));
        assertThatNullPointerException().isThrownBy(() -> new Coordinates(BigDecimal.ZERO, null));
    }

    @Test
    void coordinates_should_be_equals_when_numerically_equal() {
        Coordinates coordinates = new Coordinates(BigDecimal.valueOf(987.1234), BigDecimal.valueOf(-0.1111));
        assertThat(coordinates).isEqualTo(coordinates);

        BigDecimal ONE_POINT_ZERO = new BigDecimal("1.0");
        BigDecimal MINUS_ZERO = BigDecimal.ZERO.negate();

        Coordinates coordinates01 = new Coordinates(BigDecimal.ZERO, BigDecimal.ONE);
        assertThat(new Coordinates(MINUS_ZERO, ONE_POINT_ZERO))
                .isEqualTo(coordinates01)
                .hasSameHashCodeAs(coordinates01);

        Coordinates coordinates10 = new Coordinates(BigDecimal.ONE, BigDecimal.ZERO);
        assertThat(new Coordinates(ONE_POINT_ZERO, MINUS_ZERO))
                .isEqualTo(coordinates10)
                .hasSameHashCodeAs(coordinates10);
    }

    @Test
    void should_not_equal() {
        assertThat(new Coordinates(BigDecimal.ONE, BigDecimal.TEN))
                .isNotEqualTo(null)
                .isNotEqualTo(BigDecimal.valueOf(11))
                .isNotEqualTo(new Coordinates(BigDecimal.ONE, BigDecimal.ONE))
                .isNotEqualTo(new Coordinates(BigDecimal.TEN, BigDecimal.TEN));
    }

    @Test
    void valueOf_and_getters() {
        double latitude = Math.E;
        double longitude = Math.PI;
        Coordinates coordinates = Coordinates.of(latitude, longitude);
        assertThat(coordinates.latitude()).isEqualTo(BigDecimal.valueOf(latitude));
        assertThat(coordinates.longitude()).isEqualTo(BigDecimal.valueOf(longitude));
    }
}
