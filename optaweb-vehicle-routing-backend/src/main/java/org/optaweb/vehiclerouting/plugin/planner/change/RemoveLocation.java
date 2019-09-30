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

import java.util.ArrayList;
import java.util.Objects;

import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.solver.ProblemFactChange;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningLocation;
import org.optaweb.vehiclerouting.plugin.planner.domain.VehicleRoutingSolution;

public class RemoveLocation implements ProblemFactChange<VehicleRoutingSolution> {

    private final PlanningLocation removedLocation;

    public RemoveLocation(PlanningLocation removedLocation) {
        this.removedLocation = Objects.requireNonNull(removedLocation);
    }

    @Override
    public void doChange(ScoreDirector<VehicleRoutingSolution> scoreDirector) {
        VehicleRoutingSolution workingSolution = scoreDirector.getWorkingSolution();

        // Look up a working copy of the location
        PlanningLocation workingLocation = scoreDirector.lookUpWorkingObject(removedLocation);
        if (workingLocation == null) {
            throw new IllegalStateException("Can't look up a working copy of " + removedLocation);
        }

        // TODO think if we can fail fast when user forgets to make the clone (PLANNER)
        // Shallow-clone fact list (facts and fact collections are not planning-cloned).
        // To learn more about problem fact changes, see:
        // https://docs.jboss.org/optaplanner/release/latest/optaplanner-docs/html_single/#problemFactChangeExample
        workingSolution.setLocationList(new ArrayList<>(workingSolution.getLocationList()));

        // Remove the location
        scoreDirector.beforeProblemFactRemoved(workingLocation);
        if (!workingSolution.getLocationList().remove(workingLocation)) {
            throw new IllegalStateException(
                    "Working solution's locationList "
                            + workingSolution.getLocationList()
                            + " doesn't contain the workingLocation ("
                            + workingLocation
                            + "). This is a bug!"
            );
        }
        scoreDirector.afterProblemFactRemoved(workingLocation);

        scoreDirector.triggerVariableListeners();
    }
}
