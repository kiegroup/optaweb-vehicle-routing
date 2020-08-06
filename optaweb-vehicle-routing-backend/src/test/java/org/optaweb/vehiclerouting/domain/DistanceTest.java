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

package org.optaweb.vehiclerouting.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.Test;

class DistanceTest {

    @Test
    void distance_millis_should_be_same_as_the_given_value() {
        long millis = 123_999;
        assertThat(Distance.ofMillis(millis).millis()).isEqualTo(millis);
    }

    @Test
    void toString_should_contain_units_and_be_human_readable() {
        assertThat(Distance.ofMillis(3600_000 * 37 + 60_000 * 3 + 24_000)).hasToString("37h 3m 24s 0ms");
        assertThat(Distance.ofMillis(3601_000)).hasToString("1h 0m 1s 0ms");
        assertThat(Distance.ofMillis(5_123)).hasToString("0h 0m 5s 123ms");
    }

    @Test
    void time_must_be_positive_or_zero() {
        assertThatIllegalArgumentException().isThrownBy(() -> Distance.ofMillis(-1)).withMessageContaining("(-1)");
        assertThatCode(() -> Distance.ofMillis(0)).doesNotThrowAnyException();
    }

    @Test
    void equals_hashCode() {
        long millis = 37;
        Distance distance = Distance.ofMillis(millis);
        assertThat(distance)
                .isEqualTo(distance)
                .isEqualTo(Distance.ofMillis(millis))
                .isNotEqualTo(null)
                .isNotEqualTo(millis)
                .isNotEqualTo(Distance.ofMillis(millis + 1))
                .hasSameHashCodeAs(Distance.ofMillis(millis));
    }
}
