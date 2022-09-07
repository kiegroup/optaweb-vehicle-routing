package org.optaweb.vehiclerouting.plugin.planner;

import static org.optaplanner.core.api.score.stream.ConstraintCollectors.sum;

import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVisit;

public class VehicleRoutingConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                vehicleCapacity(constraintFactory),
                distanceFromPreviousStandstill(constraintFactory),
                distanceFromLastVisitToDepot(constraintFactory)
        };
    }

    Constraint vehicleCapacity(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(PlanningVisit.class)
                .groupBy(PlanningVisit::getVehicle, sum(PlanningVisit::getDemand))
                .filter((vehicle, demand) -> demand > vehicle.getCapacity())
                .penalizeLong(HardSoftLongScore.ONE_HARD,
                        (vehicle, demand) -> demand - vehicle.getCapacity())
                .asConstraint("vehicle capacity");
    }

    Constraint distanceFromPreviousStandstill(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(PlanningVisit.class)
                .penalizeLong(HardSoftLongScore.ONE_SOFT,
                        PlanningVisit::distanceFromPreviousStandstill)
                .asConstraint("distance from previous standstill");
    }

    Constraint distanceFromLastVisitToDepot(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(PlanningVisit.class)
                .filter(PlanningVisit::isLast)
                .penalizeLong(HardSoftLongScore.ONE_SOFT,
                        PlanningVisit::distanceToDepot)
                .asConstraint("distance from last visit to depot");
    }
}
