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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class DistanceEntityTest {

    @Test
    void constructor_params_must_not_be_null() {
        DistanceKey dKey = new DistanceKey(1, 2);
        assertThatNullPointerException().isThrownBy(() -> new DistanceEntity(null, 10.0));
        assertThatNullPointerException().isThrownBy(() -> new DistanceEntity(dKey, null));
    }

    @Test
    void equals() {
        final long from = 10;
        final long to = 2000;
        final DistanceKey distanceKey = new DistanceKey(from, to);
        final double distance = 5.0001;

        DistanceEntity distanceEntity = new DistanceEntity(distanceKey, distance);
        assertThat(distanceEntity).isEqualTo(distanceEntity);
        assertThat(distanceEntity).isEqualTo(new DistanceEntity(new DistanceKey(from, to), distance));

        assertThat(distanceEntity).isNotEqualTo(null);
        assertThat(distanceEntity).isNotEqualTo(distanceKey);
        assertThat(distanceEntity).isNotEqualTo(new DistanceEntity(distanceKey, distance + 1));
        assertThat(distanceEntity).isNotEqualTo(new DistanceEntity(new DistanceKey(to, from), distance));
    }
}
