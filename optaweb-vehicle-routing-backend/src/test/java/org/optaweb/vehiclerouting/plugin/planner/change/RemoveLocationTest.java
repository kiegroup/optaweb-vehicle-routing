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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.domain.location.Location;
import org.optaplanner.examples.vehiclerouting.domain.location.RoadLocation;
import org.optaweb.vehiclerouting.plugin.planner.SolutionUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RemoveLocationTest {

    @Mock
    private ScoreDirector<VehicleRoutingSolution> scoreDirector;

    @Test
    public void remove_location() {
        VehicleRoutingSolution solution = SolutionUtil.emptySolution();
        when(scoreDirector.getWorkingSolution()).thenReturn(solution);

        Location location = new RoadLocation(1, 2.0, 3.0);
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
}
