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
