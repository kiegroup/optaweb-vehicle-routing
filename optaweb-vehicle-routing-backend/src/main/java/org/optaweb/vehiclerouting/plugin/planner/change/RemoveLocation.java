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
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.domain.location.Location;

public class RemoveLocation implements ProblemFactChange<VehicleRoutingSolution> {

    private final Location location;

    public RemoveLocation(Location location) {
        this.location = Objects.requireNonNull(location);
    }

    @Override
    public void doChange(ScoreDirector<VehicleRoutingSolution> scoreDirector) {
        VehicleRoutingSolution workingSolution = scoreDirector.getWorkingSolution();

        Location workingLocation = scoreDirector.lookUpWorkingObject(location);
        if (workingLocation == null) {
            throw new IllegalStateException("Can't look up working copy of " + location);
        }
        // shallow clone fact list
        // TODO think if we can fail fast when user forgets to make the clone (PLANNER)
        workingSolution.setLocationList(new ArrayList<>(workingSolution.getLocationList()));
        scoreDirector.beforeProblemFactRemoved(workingLocation);
        if (!workingSolution.getLocationList().remove(workingLocation)) {
            throw new IllegalStateException("This is a bug.");
        }
        scoreDirector.afterProblemFactRemoved(workingLocation);

        scoreDirector.triggerVariableListeners();
    }
}
