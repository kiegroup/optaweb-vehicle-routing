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
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.optaplanner.test.api.solver.change.MockProblemChangeDirector;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVehicle;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVehicleFactory;
import org.optaweb.vehiclerouting.plugin.planner.domain.SolutionFactory;
import org.optaweb.vehiclerouting.plugin.planner.domain.VehicleRoutingSolution;

class AddVehicleTest {

    @Test
    void add_vehicle_should_add_vehicle() {
        VehicleRoutingSolution solution = SolutionFactory.emptySolution();
        MockProblemChangeDirector mockProblemChangeDirector = spy(new MockProblemChangeDirector());

        PlanningVehicle vehicle = PlanningVehicleFactory.testVehicle(1);
        AddVehicle addVehicle = new AddVehicle(vehicle);
        addVehicle.doChange(solution, mockProblemChangeDirector);

        assertThat(solution.getVehicleList()).containsExactly(vehicle);

        verify(mockProblemChangeDirector).addProblemFact(same(vehicle), any());
    }
}
