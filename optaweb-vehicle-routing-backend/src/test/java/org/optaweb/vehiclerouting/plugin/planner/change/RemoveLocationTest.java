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
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningLocation;
import org.optaweb.vehiclerouting.plugin.planner.domain.SolutionFactory;
import org.optaweb.vehiclerouting.plugin.planner.domain.VehicleRoutingSolution;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoveLocationTest {

    @Mock
    private ScoreDirector<VehicleRoutingSolution> scoreDirector;

    @Test
    void remove_location() {
        VehicleRoutingSolution solution = SolutionFactory.emptySolution();
        when(scoreDirector.getWorkingSolution()).thenReturn(solution);

        PlanningLocation location = new PlanningLocation(1, 2.0, 3.0);
        solution.getLocationList().add(location);

        when(scoreDirector.lookUpWorkingObject(location)).thenReturn(location);

        // do change
        RemoveLocation removeLocation = new RemoveLocation(location);
        removeLocation.doChange(scoreDirector);

        verify(scoreDirector).beforeProblemFactRemoved(location);
        verify(scoreDirector).afterProblemFactRemoved(location);
        assertThat(solution.getLocationList()).isEmpty();

        verify(scoreDirector).triggerVariableListeners();
    }

    @Test
    void fail_fast_if_working_solution_location_list_does_not_contain_working_location() {
        VehicleRoutingSolution solution = SolutionFactory.emptySolution();

        long removedId = 111L;
        PlanningLocation removedLocation = new PlanningLocation(removedId, 0, 1);
        removedLocation.setId(removedId);
        long wrongId = 222L;
        PlanningLocation wrongLocation = new PlanningLocation(wrongId, 1, 0);
        wrongLocation.setId(wrongId);
        solution.getLocationList().add(wrongLocation);

        when(scoreDirector.getWorkingSolution()).thenReturn(solution);
        when(scoreDirector.lookUpWorkingObject(removedLocation)).thenReturn(removedLocation);

        // do change
        RemoveLocation removeLocation = new RemoveLocation(removedLocation);
        assertThatIllegalStateException()
                .isThrownBy(() -> removeLocation.doChange(scoreDirector))
                .withMessageMatching(".*List .*" + wrongId + ".* doesn't contain the working.*" + removedId + ".*");
    }

    @Test
    void fail_fast_if_working_object_is_null() {
        when(scoreDirector.getWorkingSolution()).thenReturn(SolutionFactory.emptySolution());

        assertThatIllegalStateException()
                .isThrownBy(() -> new RemoveLocation(new PlanningLocation(1, 2, 3)).doChange(scoreDirector))
                .withMessageContaining("working copy of");
    }
}
