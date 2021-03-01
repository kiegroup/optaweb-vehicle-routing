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
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVehicleFactory.testVehicle;
import static org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVisitFactory.testVisit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVisit;
import org.optaweb.vehiclerouting.plugin.planner.domain.SolutionFactory;
import org.optaweb.vehiclerouting.plugin.planner.domain.VehicleRoutingSolution;

@ExtendWith(MockitoExtension.class)
class RemoveVisitTest {

    @Mock
    private ScoreDirector<VehicleRoutingSolution> scoreDirector;

    @Test
    void remove_last_visit() {
        VehicleRoutingSolution solution = SolutionFactory.emptySolution();
        when(scoreDirector.getWorkingSolution()).thenReturn(solution);

        PlanningVisit removedVisit = testVisit(1);
        PlanningVisit otherVisit = testVisit(2);
        solution.getVisitList().add(otherVisit);
        solution.getVisitList().add(removedVisit);

        // V -> other -> removed
        otherVisit.setPreviousStandstill(testVehicle(10));
        otherVisit.setNextVisit(removedVisit);
        removedVisit.setPreviousStandstill(otherVisit);

        when(scoreDirector.lookUpWorkingObject(removedVisit)).thenReturn(removedVisit);

        // do change
        RemoveVisit removeVisit = new RemoveVisit(removedVisit);
        removeVisit.doChange(scoreDirector);

        verify(scoreDirector).beforeEntityRemoved(removedVisit);
        verify(scoreDirector).afterEntityRemoved(removedVisit);
        assertThat(solution.getVisitList()).containsExactly(otherVisit);

        verify(scoreDirector).triggerVariableListeners();
    }

    @Test
    void remove_middle_visit() {
        VehicleRoutingSolution solution = SolutionFactory.emptySolution();
        when(scoreDirector.getWorkingSolution()).thenReturn(solution);

        PlanningVisit firstVisit = testVisit(1);
        PlanningVisit middleVisit = testVisit(2);
        PlanningVisit lastVisit = testVisit(3);
        solution.getVisitList().add(firstVisit);
        solution.getVisitList().add(lastVisit);
        solution.getVisitList().add(middleVisit);

        // V -> first -> removed -> last
        firstVisit.setPreviousStandstill(testVehicle(1));
        firstVisit.setNextVisit(middleVisit);
        middleVisit.setPreviousStandstill(firstVisit);
        middleVisit.setNextVisit(lastVisit);
        lastVisit.setPreviousStandstill(middleVisit);

        PlanningVisit removedVisit = testVisit(2);
        when(scoreDirector.lookUpWorkingObject(removedVisit)).thenReturn(middleVisit);

        // do change
        RemoveVisit removeVisit = new RemoveVisit(removedVisit);
        removeVisit.doChange(scoreDirector);

        verify(scoreDirector).beforeVariableChanged(lastVisit, "previousStandstill");
        verify(scoreDirector).afterVariableChanged(lastVisit, "previousStandstill");
        verify(scoreDirector).beforeEntityRemoved(middleVisit);
        verify(scoreDirector).afterEntityRemoved(middleVisit);
        assertThat(solution.getVisitList())
                .hasSize(2)
                .containsOnly(firstVisit, lastVisit);

        // V -> first -> removed -> last
        assertThat(lastVisit.getPreviousStandstill()).isEqualTo(firstVisit);

        verify(scoreDirector).triggerVariableListeners();
    }

    @Test
    void fail_fast_if_working_solution_visit_list_does_not_contain_working_visit() {
        VehicleRoutingSolution solution = SolutionFactory.emptySolution();

        long removedId = 111L;
        PlanningVisit removedVisit = testVisit(removedId);
        long wrongId = 222L;
        PlanningVisit wrongVisit = testVisit(wrongId);
        wrongVisit.setPreviousStandstill(testVisit(10));
        removedVisit.setNextVisit(wrongVisit);
        solution.getVisitList().add(wrongVisit);

        when(scoreDirector.getWorkingSolution()).thenReturn(solution);
        when(scoreDirector.lookUpWorkingObject(removedVisit)).thenReturn(removedVisit);

        // do change
        RemoveVisit removeVisit = new RemoveVisit(removedVisit);
        assertThatIllegalStateException()
                .isThrownBy(() -> removeVisit.doChange(scoreDirector))
                .withMessageMatching(".*List .*" + wrongId + ".* doesn't contain the working.*" + removedId + ".*");
    }

    @Test
    void fail_fast_if_working_object_is_null() {
        when(scoreDirector.getWorkingSolution()).thenReturn(SolutionFactory.emptySolution());

        assertThatIllegalStateException()
                .isThrownBy(() -> new RemoveVisit(testVisit(0)).doChange(scoreDirector))
                .withMessageContaining("working copy of");
    }
}
