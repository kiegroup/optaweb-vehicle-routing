package org.optaweb.vehiclerouting.plugin.planner.change;

import java.util.Objects;

import org.optaplanner.core.api.solver.change.ProblemChange;
import org.optaplanner.core.api.solver.change.ProblemChangeDirector;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVehicle;
import org.optaweb.vehiclerouting.plugin.planner.domain.VehicleRoutingSolution;

public class ChangeVehicleCapacity implements ProblemChange<VehicleRoutingSolution> {

    private final PlanningVehicle vehicle;

    public ChangeVehicleCapacity(PlanningVehicle vehicle) {
        this.vehicle = Objects.requireNonNull(vehicle);
    }

    @Override
    public void doChange(VehicleRoutingSolution workingSolution, ProblemChangeDirector problemChangeDirector) {
        // No need to clone the workingVehicle because it is a planning entity, so it is already planning-cloned.
        // To learn more about problem fact changes, see:
        // https://www.optaplanner.org/docs/optaplanner/latest/repeated-planning/repeated-planning.html#problemChangeExample
        problemChangeDirector.changeProblemProperty(vehicle,
                workingVehicle -> workingVehicle.setCapacity(vehicle.getCapacity()));
    }
}
