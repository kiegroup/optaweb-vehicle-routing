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

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LatLngTest {

    @Test
    public void constructor_params_must_not_be_null() {
        assertThatThrownBy(() -> new LatLng(null, BigDecimal.ZERO)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new LatLng(BigDecimal.ZERO, null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void latngs_should_be_equals_when_numerically_equal() {
        BigDecimal ONE_POINT_ZERO = new BigDecimal("1.0");
        BigDecimal MINUS_ZERO = BigDecimal.ZERO.negate();
        assertThat(new LatLng(MINUS_ZERO, ONE_POINT_ZERO))
                .isEqualTo(new LatLng(BigDecimal.ZERO, BigDecimal.ONE));
    }
}
