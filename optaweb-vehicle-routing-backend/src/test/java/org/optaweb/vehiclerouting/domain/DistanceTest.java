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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class DistanceTest {

    @Test
    void distance_seconds_should_be_same_as_the_give_value() {
        long seconds = 999;
        assertThat(Distance.ofSeconds(seconds).seconds()).isEqualTo(seconds);
    }

    @Test
    void string_should_end_with_unit() {
        assertThat(Distance.ofSeconds(312)).hasToString("312 s");
    }

    @Test
    void seconds_must_be_positive_or_zero() {
        assertThatIllegalArgumentException().isThrownBy(() -> Distance.ofSeconds(-1));
        assertThatCode(() -> Distance.ofSeconds(0)).doesNotThrowAnyException();
    }

    @Test
    void equals_hashCode() {
        long seconds = 37;
        Distance distance = Distance.ofSeconds(seconds);
        assertThat(distance).isEqualTo(distance);
        assertThat(distance).isEqualTo(Distance.ofSeconds(seconds));
        assertThat(distance).isNotEqualTo(null);
        assertThat(distance).isNotEqualTo(seconds);
        assertThat(distance).isNotEqualTo(Distance.ofSeconds(seconds + 1));

        assertThat(distance).hasSameHashCodeAs(Distance.ofSeconds(seconds));
    }
}
