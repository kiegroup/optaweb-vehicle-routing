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

package org.optaweb.vehiclerouting.plugin.planner.change;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.optaplanner.core.api.solver.change.ProblemChangeDirector;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVehicle;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVehicleFactory;
import org.optaweb.vehiclerouting.plugin.planner.domain.SolutionFactory;

@ExtendWith(MockitoExtension.class)
class ChangeVehicleCapacityTest {

    @Mock
    private ProblemChangeDirector problemChangeDirector;

    @Test
    void change_vehicle_capacity() {
        int oldCapacity = 100;
        int newCapacity = 50;

        PlanningVehicle workingVehicle = PlanningVehicleFactory.testVehicle(1, oldCapacity);
        PlanningVehicle changeVehicle = PlanningVehicleFactory.testVehicle(2, newCapacity);
        when(problemChangeDirector.lookUpWorkingObjectOrFail(changeVehicle)).thenReturn(workingVehicle);

        // do change
        new ChangeVehicleCapacity(changeVehicle).doChange(SolutionFactory.emptySolution(), problemChangeDirector);
        assertThat(workingVehicle.getCapacity()).isEqualTo(newCapacity);
    }
}
