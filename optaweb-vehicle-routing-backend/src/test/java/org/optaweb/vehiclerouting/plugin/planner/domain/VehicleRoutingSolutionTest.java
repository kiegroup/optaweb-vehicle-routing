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

package org.optaweb.vehiclerouting.plugin.planner.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;

import static org.assertj.core.api.Assertions.assertThat;

class VehicleRoutingSolutionTest {

    private VehicleRoutingSolution solution;

    @BeforeEach
    void setUp() {
        solution = SolutionFactory.emptySolution();
    }

    @Test
    void should_return_distance_in_time() {
        solution.setDistanceUnitOfMeasurement("sec");
        solution.setScore(HardSoftLongScore.of(0L, -3661001L));
        String distanceString = solution.getDistanceString(null);
        assertThat(distanceString).isEqualTo("1h 1m 1s 1ms");
    }

    @Test
    void should_return_distance_in_meters() {
        solution.setDistanceUnitOfMeasurement("meter");
        solution.setScore(HardSoftLongScore.of(0L, -100100));
        String distanceString = solution.getDistanceString(null);
        assertThat(distanceString).isEqualTo("100km 100m");
    }
}
