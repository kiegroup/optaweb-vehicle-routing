package org.optaweb.vehiclerouting.plugin.planner.change;

import java.util.Objects;

import org.optaplanner.core.api.solver.change.ProblemChange;
import org.optaplanner.core.api.solver.change.ProblemChangeDirector;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVehicle;
import org.optaweb.vehiclerouting.plugin.planner.domain.VehicleRoutingSolution;

public class AddVehicle implements ProblemChange<VehicleRoutingSolution> {

    private final PlanningVehicle vehicle;

    public AddVehicle(PlanningVehicle vehicle) {
        this.vehicle = Objects.requireNonNull(vehicle);
    }

    @Override
    public void doChange(VehicleRoutingSolution workingSolution, ProblemChangeDirector problemChangeDirector) {
        problemChangeDirector.addProblemFact(vehicle, workingSolution.getVehicleList()::add);
    }
}
