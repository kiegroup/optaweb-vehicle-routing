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
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVisit;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVisitFactory;
import org.optaweb.vehiclerouting.plugin.planner.domain.SolutionFactory;
import org.optaweb.vehiclerouting.plugin.planner.domain.VehicleRoutingSolution;

class AddVisitTest {

    @Test
    void add_visit_should_add_location_and_create_visit() {
        VehicleRoutingSolution solution = SolutionFactory.emptySolution();
        MockProblemChangeDirector mockProblemChangeDirector = spy(new MockProblemChangeDirector());

        PlanningVisit visit = PlanningVisitFactory.testVisit(1);
        AddVisit addVisit = new AddVisit(visit);
        addVisit.doChange(solution, mockProblemChangeDirector);

        verify(mockProblemChangeDirector).addEntity(same(visit), any());
        assertThat(solution.getVisitList()).containsExactly(visit);
    }
}
