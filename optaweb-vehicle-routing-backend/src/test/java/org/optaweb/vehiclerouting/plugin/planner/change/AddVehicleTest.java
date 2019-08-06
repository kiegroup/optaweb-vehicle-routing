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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.examples.vehiclerouting.domain.Vehicle;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaweb.vehiclerouting.plugin.planner.SolutionFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddVehicleTest {

    @Mock
    private ScoreDirector<VehicleRoutingSolution> scoreDirector;

    @Test
    void add_vehicle_should_add_vehicle() {
        VehicleRoutingSolution solution = SolutionFactory.emptySolution();
        when(scoreDirector.getWorkingSolution()).thenReturn(solution);

        Vehicle vehicle = new Vehicle();
        AddVehicle addVehicle = new AddVehicle(vehicle);
        addVehicle.doChange(scoreDirector);

        assertThat(solution.getVehicleList()).containsExactly(vehicle);

        verify(scoreDirector).beforeProblemFactAdded(vehicle);
        verify(scoreDirector).afterProblemFactAdded(vehicle);
        verify(scoreDirector).triggerVariableListeners();
    }
}
