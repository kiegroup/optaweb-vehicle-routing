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

import org.optaplanner.core.api.solver.change.ProblemChange;
import org.optaplanner.core.api.solver.change.ProblemChangeDirector;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVisit;
import org.optaweb.vehiclerouting.plugin.planner.domain.VehicleRoutingSolution;

public class RemoveVisit implements ProblemChange<VehicleRoutingSolution> {

    private final PlanningVisit planningVisit;

    public RemoveVisit(PlanningVisit planningVisit) {
        this.planningVisit = Objects.requireNonNull(planningVisit);
    }

    @Override
    public void doChange(VehicleRoutingSolution workingSolution, ProblemChangeDirector problemChangeDirector) {
        // Look up a working copy of the visit
        PlanningVisit workingVisit = problemChangeDirector.lookUpWorkingObjectOrFail(planningVisit);

        // Fix the next visit and set its previousStandstill to the removed visit's previousStandstill
        PlanningVisit nextVisit = workingVisit.getNextVisit();
        if (nextVisit != null) { // otherwise it's the last visit
            problemChangeDirector.changeVariable(nextVisit, "previousStandstill",
                    workingNextVisit -> workingNextVisit.setPreviousStandstill(workingVisit.getPreviousStandstill()));
        }

        // No need to clone the visitList because it is a planning entity collection, so it is already planning-cloned.
        // To learn more about problem fact changes, see:
        // https://www.optaplanner.org/docs/optaplanner/latest/repeated-planning/repeated-planning.html#problemChangeExample

        // Remove the visit
        problemChangeDirector.removeEntity(planningVisit, visit -> {
            if (!workingSolution.getVisitList().remove(visit)) {
                throw new IllegalStateException(
                        "Working solution's visitList "
                                + workingSolution.getVisitList()
                                + " doesn't contain the workingVisit ("
                                + visit
                                + "). This is a bug!");
            }
        });
    }
}
