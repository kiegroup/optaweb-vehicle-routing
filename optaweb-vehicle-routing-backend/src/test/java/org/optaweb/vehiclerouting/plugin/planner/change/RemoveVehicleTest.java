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
import static org.mockito.Mockito.when;
import static org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVisitFactory.testVisit;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.optaplanner.core.api.solver.change.ProblemChangeDirector;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningDepot;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningLocationFactory;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVehicle;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVehicleFactory;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVisit;
import org.optaweb.vehiclerouting.plugin.planner.domain.SolutionFactory;
import org.optaweb.vehiclerouting.plugin.planner.domain.VehicleRoutingSolution;

@ExtendWith(MockitoExtension.class)
class RemoveVehicleTest {

    @Mock
    private ProblemChangeDirector problemChangeDirector;

    @Test
    void remove_vehicle() {
        PlanningVehicle removedVehicle = PlanningVehicleFactory.testVehicle(1);
        PlanningVehicle otherVehicle = PlanningVehicleFactory.testVehicle(2);

        PlanningDepot depot = new PlanningDepot(PlanningLocationFactory.testLocation(1));

        PlanningVisit firstVisit = testVisit(1);
        PlanningVisit lastVisit = testVisit(2);

        VehicleRoutingSolution solution = SolutionFactory.solutionFromVisits(
                Arrays.asList(removedVehicle, otherVehicle),
                depot,
                Arrays.asList(firstVisit, lastVisit));

        // V -> first -> last
        removedVehicle.setNextVisit(firstVisit);
        firstVisit.setPreviousStandstill(removedVehicle);
        firstVisit.setVehicle(removedVehicle);
        firstVisit.setNextVisit(lastVisit);
        lastVisit.setPreviousStandstill(firstVisit);
        lastVisit.setVehicle(removedVehicle);

        // do change
        when(problemChangeDirector.lookUpWorkingObjectOrFail(removedVehicle)).thenReturn(removedVehicle);
        new RemoveVehicle(removedVehicle).doChange(solution, problemChangeDirector);

        assertThat(firstVisit.getPreviousStandstill()).isNull();
        assertThat(lastVisit.getPreviousStandstill()).isNull();
        assertThat(solution.getVehicleList()).containsExactly(otherVehicle);
    }

    @Test
    void fail_fast_if_working_solution_vehicle_list_does_not_contain_working_vehicle() {
        long removedId = 111L;
        long wrongId = 222L;
        PlanningVehicle removedVehicle = PlanningVehicleFactory.testVehicle(removedId);
        PlanningVehicle wrongVehicle = PlanningVehicleFactory.testVehicle(wrongId);

        PlanningDepot depot = new PlanningDepot(PlanningLocationFactory.testLocation(1));

        VehicleRoutingSolution solution = SolutionFactory.solutionFromVisits(
                Arrays.asList(wrongVehicle),
                depot,
                Collections.emptyList());

        when(problemChangeDirector.lookUpWorkingObjectOrFail(removedVehicle)).thenReturn(removedVehicle);

        // do change
        RemoveVehicle removeVehicle = new RemoveVehicle(removedVehicle);
        assertThatIllegalStateException()
                .isThrownBy(() -> removeVehicle.doChange(solution, problemChangeDirector))
                .withMessageMatching(".*List .*" + wrongId + ".* doesn't contain the working.*" + removedId + ".*");
    }

}
