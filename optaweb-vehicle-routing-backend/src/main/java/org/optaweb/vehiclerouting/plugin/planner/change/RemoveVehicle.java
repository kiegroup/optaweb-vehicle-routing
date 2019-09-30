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

import java.util.Objects;

import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.solver.ProblemFactChange;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVehicle;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVisit;
import org.optaweb.vehiclerouting.plugin.planner.domain.VehicleRoutingSolution;

public class RemoveVehicle implements ProblemFactChange<VehicleRoutingSolution> {

    private final PlanningVehicle removedVehicle;

    public RemoveVehicle(PlanningVehicle removedVehicle) {
        this.removedVehicle = Objects.requireNonNull(removedVehicle);
    }

    @Override
    public void doChange(ScoreDirector<VehicleRoutingSolution> scoreDirector) {
        VehicleRoutingSolution workingSolution = scoreDirector.getWorkingSolution();

        // Look up a working copy of the vehicle
        PlanningVehicle workingVehicle = scoreDirector.lookUpWorkingObject(removedVehicle);
        if (workingVehicle == null) {
            throw new IllegalStateException("Can't look up a working copy of " + removedVehicle);
        }

        // Un-initialize all visits of this vehicle
        for (PlanningVisit visit : workingVehicle.getFutureVisits()) {
            scoreDirector.beforeVariableChanged(visit, "previousStandstill");
            visit.setPreviousStandstill(null);
            scoreDirector.afterVariableChanged(visit, "previousStandstill");
        }

        // No need to clone the vehicleList because it is a planning entity collection, so it is already
        // planning-cloned.
        // To learn more about problem fact changes, see:
        // https://docs.jboss.org/optaplanner/release/latest/optaplanner-docs/html_single/#problemFactChangeExample

        // Remove the vehicle
        scoreDirector.beforeProblemFactRemoved(workingVehicle);
        if (!workingSolution.getVehicleList().remove(workingVehicle)) {
            throw new IllegalStateException(
                    "Working solution's vehicleList "
                            + workingSolution.getVehicleList()
                            + " doesn't contain the workingVehicle ("
                            + workingVehicle
                            + "). This is a bug!"
            );
        }
        scoreDirector.afterProblemFactRemoved(workingVehicle);

        scoreDirector.triggerVariableListeners();
    }
}
