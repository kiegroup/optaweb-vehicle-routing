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
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVisit;
import org.optaweb.vehiclerouting.plugin.planner.domain.VehicleRoutingSolution;

public class RemoveVisit implements ProblemFactChange<VehicleRoutingSolution> {

    private final PlanningVisit planningVisit;

    public RemoveVisit(PlanningVisit planningVisit) {
        this.planningVisit = Objects.requireNonNull(planningVisit);
    }

    @Override
    public void doChange(ScoreDirector<VehicleRoutingSolution> scoreDirector) {
        VehicleRoutingSolution workingSolution = scoreDirector.getWorkingSolution();

        // Look up a working copy of the visit
        PlanningVisit workingVisit = scoreDirector.lookUpWorkingObject(planningVisit);
        if (workingVisit == null) {
            throw new IllegalStateException("Can't look up a working copy of " + planningVisit);
        }

        // Fix the next visit and set its previousStandstill to the removed visit's previousStandstill
        for (PlanningVisit nextVisit : workingSolution.getVisitList()) {
            if (nextVisit.getPreviousStandstill().equals(workingVisit)) {
                scoreDirector.beforeVariableChanged(nextVisit, "previousStandstill");
                nextVisit.setPreviousStandstill(workingVisit.getPreviousStandstill());
                scoreDirector.afterVariableChanged(nextVisit, "previousStandstill");
                break;
            }
        }

        // No need to clone the visitList because it is a planning entity collection, so it is already planning-cloned.
        // To learn more about problem fact changes, see:
        // https://docs.jboss.org/optaplanner/release/latest/optaplanner-docs/html_single/#problemFactChangeExample

        // Remove the visit
        scoreDirector.beforeEntityRemoved(workingVisit);
        if (!workingSolution.getVisitList().remove(workingVisit)) {
            throw new IllegalStateException(
                    "Working solution's visitList "
                            + workingSolution.getVisitList()
                            + " doesn't contain the workingVisit ("
                            + workingVisit
                            + "). This is a bug!"
            );
        }
        scoreDirector.afterEntityRemoved(workingVisit);

        scoreDirector.triggerVariableListeners();
    }
}
