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
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningDepot;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningLocation;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVehicle;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVisit;
import org.optaweb.vehiclerouting.plugin.planner.domain.SolutionFactory;
import org.optaweb.vehiclerouting.plugin.planner.domain.VehicleRoutingSolution;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoveVisitTest {

    @Mock
    private ScoreDirector<VehicleRoutingSolution> scoreDirector;

    @Test
    void remove_last_visit() {
        VehicleRoutingSolution solution = SolutionFactory.emptySolution();
        when(scoreDirector.getWorkingSolution()).thenReturn(solution);

        PlanningVisit removedVisit = visit(1);
        PlanningVisit otherVisit = visit(2);
        solution.getVisitList().add(otherVisit);
        solution.getVisitList().add(removedVisit);

        // V -> other -> removed
        otherVisit.setPreviousStandstill(visit(10));
        otherVisit.setNextVisit(removedVisit);
        removedVisit.setPreviousStandstill(otherVisit);

        when(scoreDirector.lookUpWorkingObject(removedVisit)).thenReturn(removedVisit);

        // do change
        RemoveVisit removeVisit = new RemoveVisit(removedVisit);
        removeVisit.doChange(scoreDirector);

        verify(scoreDirector).beforeEntityRemoved(any(PlanningVisit.class));
        verify(scoreDirector).afterEntityRemoved(any(PlanningVisit.class));
        assertThat(solution.getVisitList()).containsExactly(otherVisit);

        verify(scoreDirector).triggerVariableListeners();
    }

    @Test
    void remove_middle_visit() {
        VehicleRoutingSolution solution = SolutionFactory.emptySolution();
        when(scoreDirector.getWorkingSolution()).thenReturn(solution);

        PlanningVisit firstVisit = visit(1);
        PlanningVisit removedVisit = visit(2);
        PlanningVisit lastVisit = visit(3);
        solution.getVisitList().add(firstVisit);
        solution.getVisitList().add(lastVisit);
        solution.getVisitList().add(removedVisit);

        // V -> first -> removed -> last
        firstVisit.setPreviousStandstill(planningVehicle(1));
        firstVisit.setNextVisit(removedVisit);
        removedVisit.setPreviousStandstill(firstVisit);
        removedVisit.setNextVisit(lastVisit);
        lastVisit.setPreviousStandstill(removedVisit);

        when(scoreDirector.lookUpWorkingObject(removedVisit)).thenReturn(removedVisit);

        // do change
        RemoveVisit removeVisit = new RemoveVisit(removedVisit);
        removeVisit.doChange(scoreDirector);

        // TODO make this more accurate once Customer overrides equals()
        verify(scoreDirector).beforeVariableChanged(any(PlanningVisit.class), anyString());
        verify(scoreDirector).afterVariableChanged(any(PlanningVisit.class), anyString());
        verify(scoreDirector).beforeEntityRemoved(any(PlanningVisit.class));
        verify(scoreDirector).afterEntityRemoved(any(PlanningVisit.class));
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
        PlanningVisit removedVisit = visit(removedId);
        long wrongId = 222L;
        PlanningVisit wrongVisit = visit(wrongId);
        wrongVisit.setPreviousStandstill(visit(10));
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
                .isThrownBy(() -> new RemoveVisit(visit(0)).doChange(scoreDirector))
                .withMessageContaining("working copy of");
    }

    private static PlanningVehicle planningVehicle(long id) {
        PlanningVehicle vehicle = new PlanningVehicle();
        vehicle.setDepot(new PlanningDepot());
        vehicle.getDepot().setLocation(new PlanningLocation());
        return vehicle;
    }

    private static PlanningVisit visit(long id) {
        PlanningLocation location = new PlanningLocation(1000000 + id, id, id);
        PlanningVisit visit = new PlanningVisit();
        visit.setId(id);
        visit.setLocation(location);
        return visit;
    }
}
