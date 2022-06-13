package org.optaweb.vehiclerouting.plugin.planner.change;

import java.util.Objects;

import org.optaplanner.core.api.solver.change.ProblemChange;
import org.optaplanner.core.api.solver.change.ProblemChangeDirector;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVisit;
import org.optaweb.vehiclerouting.plugin.planner.domain.VehicleRoutingSolution;

public class AddVisit implements ProblemChange<VehicleRoutingSolution> {

    private final PlanningVisit visit;

    public AddVisit(PlanningVisit visit) {
        this.visit = Objects.requireNonNull(visit);
    }

    @Override
    public void doChange(VehicleRoutingSolution workingSolution, ProblemChangeDirector problemChangeDirector) {
        problemChangeDirector.addEntity(visit, workingSolution.getVisitList()::add);
    }
}
