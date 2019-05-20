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

package org.optaweb.vehiclerouting.service.region;

import org.junit.Test;
import org.optaweb.vehiclerouting.domain.Coordinates;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class BoundingBoxTest {

    @Test
    public void validate_southwest_and_northeast_arguments() {
        // 1───┐
        // │ ↘ │
        // └───2
        assertThatIllegalArgumentException().isThrownBy(() -> new BoundingBox(
                Coordinates.valueOf(9.9, -1.0), // NW
                Coordinates.valueOf(1.0, 1.01)  // SE
        )).withMessageMatching(".*\\(9\\.9N.*\\(1\\.0N.*");
        // 2───┐
        // │ ↖ │
        // └───1
        assertThatIllegalArgumentException().isThrownBy(() -> new BoundingBox(
                Coordinates.valueOf(-1.0, 9.9), // SE
                Coordinates.valueOf(1.01, 1.0)  // NW
        )).withMessageMatching(".*\\(9\\.9E.*\\(1\\.0E.*");
        // ┌───1
        // │ ↙ │
        // 2───┘
        assertThatIllegalArgumentException().isThrownBy(() -> new BoundingBox(
                Coordinates.valueOf(9.9, 9.9), // NE
                Coordinates.valueOf(1.0, 1.0)  // SW
        )).withMessageMatching(".*\\(9\\.9N.*\\(1\\.0N.*");
    }

    @Test
    public void should_fail_if_bounding_box_has_zero_dimension() {
        //
        // ╶───╴
        //
        assertThatIllegalArgumentException().isThrownBy(() -> new BoundingBox(
                Coordinates.valueOf(0.0, 1.0),
                Coordinates.valueOf(0.0, 2.0)
        )).withMessageMatching(".*\\(0\\.0N.*\\(0\\.0N.*");
        //   ╷
        //   │
        //   ╵
        assertThatIllegalArgumentException().isThrownBy(() -> new BoundingBox(
                Coordinates.valueOf(0.0, 10.0),
                Coordinates.valueOf(1.0, 10.0)
        )).withMessageMatching(".*\\(10\\.0E.*\\(10\\.0E.*");
    }
}
