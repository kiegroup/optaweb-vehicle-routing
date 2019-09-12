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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoveVehicleTest {

    @Mock
    private ScoreDirector<VehicleRoutingSolution> scoreDirector;

    @Test
    void remove_vehicle() {
        VehicleRoutingSolution solution = SolutionFactory.emptySolution();
        when(scoreDirector.getWorkingSolution()).thenReturn(solution);

        PlanningLocation location = new PlanningLocation(1, 2.0, 3.0);
        PlanningDepot depot = new PlanningDepot();
        depot.setLocation(location);

        PlanningVehicle removedVehicle = new PlanningVehicle();
        removedVehicle.setId(1L);
        removedVehicle.setDepot(depot);
        PlanningVehicle otherVehicle = new PlanningVehicle();
        otherVehicle.setId(2L);
        otherVehicle.setDepot(depot);
        solution.getVehicleList().add(removedVehicle);
        solution.getVehicleList().add(otherVehicle);

        when(scoreDirector.lookUpWorkingObject(removedVehicle)).thenReturn(removedVehicle);

        PlanningVisit firstVisit = visit(1);
        PlanningVisit lastVisit = visit(2);
        solution.getVisitList().add(firstVisit);
        solution.getVisitList().add(lastVisit);

        // V -> first -> last
        removedVehicle.setNextVisit(firstVisit);
        firstVisit.setPreviousStandstill(removedVehicle);
        firstVisit.setVehicle(removedVehicle);
        firstVisit.setNextVisit(lastVisit);
        lastVisit.setPreviousStandstill(firstVisit);
        lastVisit.setVehicle(removedVehicle);

        // do change
        RemoveVehicle removeVehicle = new RemoveVehicle(removedVehicle);
        removeVehicle.doChange(scoreDirector);

        assertThat(firstVisit.getPreviousStandstill()).isNull();
        assertThat(lastVisit.getPreviousStandstill()).isNull();
        assertThat(solution.getVehicleList()).containsExactly(otherVehicle);

        verify(scoreDirector).beforeVariableChanged(firstVisit, "previousStandstill");
        verify(scoreDirector).afterVariableChanged(firstVisit, "previousStandstill");
        verify(scoreDirector).beforeVariableChanged(lastVisit, "previousStandstill");
        verify(scoreDirector).afterVariableChanged(lastVisit, "previousStandstill");
        verify(scoreDirector).beforeProblemFactRemoved(any(PlanningVehicle.class));
        verify(scoreDirector).afterProblemFactRemoved(any(PlanningVehicle.class));
        verify(scoreDirector).triggerVariableListeners();
    }

    @Test
    void fail_fast_if_working_solution_vehicle_list_does_not_contain_working_vehicle() {
        VehicleRoutingSolution solution = SolutionFactory.emptySolution();

        PlanningLocation location = new PlanningLocation(1, 2.0, 3.0);
        PlanningDepot depot = new PlanningDepot();
        depot.setLocation(location);

        long removedId = 111L;
        PlanningVehicle removedVehicle = new PlanningVehicle();
        removedVehicle.setId(removedId);
        removedVehicle.setDepot(depot);
        long wrongId = 222L;
        PlanningVehicle wrongVehicle = new PlanningVehicle();
        wrongVehicle.setId(wrongId);
        wrongVehicle.setDepot(depot);
        solution.getVehicleList().add(wrongVehicle);

        when(scoreDirector.getWorkingSolution()).thenReturn(solution);
        when(scoreDirector.lookUpWorkingObject(removedVehicle)).thenReturn(removedVehicle);

        // do change
        RemoveVehicle removeVehicle = new RemoveVehicle(removedVehicle);
        assertThatIllegalStateException()
                .isThrownBy(() -> removeVehicle.doChange(scoreDirector))
                .withMessageMatching(".*List .*" + wrongId + ".* doesn't contain the working.*" + removedId + ".*");
    }

    @Test
    void fail_fast_if_working_object_is_null() {
        when(scoreDirector.getWorkingSolution()).thenReturn(SolutionFactory.emptySolution());
        PlanningDepot depot = new PlanningDepot();
        depot.setLocation(new PlanningLocation(4L, 1, 2));
        PlanningVehicle vehicle = new PlanningVehicle();
        vehicle.setId(1L);
        vehicle.setDepot(depot);

        assertThatIllegalStateException()
                .isThrownBy(() -> new RemoveVehicle(vehicle).doChange(scoreDirector))
                .withMessageContaining("working copy of");
    }

    private static PlanningVisit visit(long id) {
        PlanningLocation location = new PlanningLocation(1000000 + id, id, id);
        PlanningVisit visit = new PlanningVisit();
        visit.setId(id);
        visit.setLocation(location);
        return visit;
    }
}
