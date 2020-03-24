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
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVisit;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVisitFactory;
import org.optaweb.vehiclerouting.plugin.planner.domain.SolutionFactory;
import org.optaweb.vehiclerouting.plugin.planner.domain.VehicleRoutingSolution;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddVisitTest {

    @Mock
    private ScoreDirector<VehicleRoutingSolution> scoreDirector;

    @Test
    void add_visit_should_add_location_and_create_visit() {
        VehicleRoutingSolution solution = SolutionFactory.emptySolution();
        when(scoreDirector.getWorkingSolution()).thenReturn(solution);

        PlanningLocation location = new PlanningLocation(1, 1.0, 2.0);
        PlanningVisit visit = PlanningVisitFactory.fromLocation(location);
        AddVisit addVisit = new AddVisit(visit);
        addVisit.doChange(scoreDirector);

        verify(scoreDirector).beforeProblemFactAdded(location);
        verify(scoreDirector).afterProblemFactAdded(location);
        assertThat(solution.getLocationList()).containsExactly(location);

        verify(scoreDirector).beforeEntityAdded(visit);
        verify(scoreDirector).afterEntityAdded(visit);
        assertThat(solution.getVisitList()).containsExactly(visit);

        verify(scoreDirector).triggerVariableListeners();
    }
}
