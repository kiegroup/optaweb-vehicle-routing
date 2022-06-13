package org.optaweb.vehiclerouting.plugin.planner.change;

import java.util.Objects;

import org.optaplanner.core.api.solver.change.ProblemChange;
import org.optaplanner.core.api.solver.change.ProblemChangeDirector;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVehicle;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVisit;
import org.optaweb.vehiclerouting.plugin.planner.domain.VehicleRoutingSolution;

public class RemoveVehicle implements ProblemChange<VehicleRoutingSolution> {

    private final PlanningVehicle removedVehicle;

    public RemoveVehicle(PlanningVehicle removedVehicle) {
        this.removedVehicle = Objects.requireNonNull(removedVehicle);
    }

    @Override
    public void doChange(VehicleRoutingSolution workingSolution, ProblemChangeDirector problemChangeDirector) {
        // Look up a working copy of the vehicle
        PlanningVehicle workingVehicle = problemChangeDirector.lookUpWorkingObjectOrFail(removedVehicle);

        // Un-initialize all visits of this vehicle
        for (PlanningVisit visit : workingVehicle.getFutureVisits()) {
            problemChangeDirector.changeVariable(visit, "previousStandstill",
                    planningVisit -> planningVisit.setPreviousStandstill(null));
        }

        // No need to clone the vehicleList because it is a planning entity collection, so it is already
        // planning-cloned.
        // To learn more about problem fact changes, see:
        // https://www.optaplanner.org/docs/optaplanner/latest/repeated-planning/repeated-planning.html#problemChangeExample

        // Remove the vehicle
        problemChangeDirector.removeProblemFact(workingVehicle, planningVehicle -> {
            if (!workingSolution.getVehicleList().remove(planningVehicle)) {
                throw new IllegalStateException(
                        "Working solution's vehicleList "
                                + workingSolution.getVehicleList()
                                + " doesn't contain the workingVehicle ("
                                + planningVehicle
                                + "). This is a bug!");
            }
        });
    }
}
