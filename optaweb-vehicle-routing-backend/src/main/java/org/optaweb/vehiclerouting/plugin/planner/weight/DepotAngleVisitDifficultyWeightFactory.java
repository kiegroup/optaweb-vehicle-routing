package org.optaweb.vehiclerouting.plugin.planner.weight;

import static java.util.Comparator.comparingDouble;
import static java.util.Comparator.comparingLong;

import java.util.Comparator;
import java.util.Objects;

import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningDepot;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningLocation;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVisit;
import org.optaweb.vehiclerouting.plugin.planner.domain.VehicleRoutingSolution;

/**
 * On large data sets, the constructed solution looks like pizza slices.
 * The order of the slices depends on the {@link PlanningLocation#angleTo} implementation.
 */
public class DepotAngleVisitDifficultyWeightFactory
        implements SelectionSorterWeightFactory<VehicleRoutingSolution, PlanningVisit> {

    @Override
    public DepotAngleVisitDifficultyWeight createSorterWeight(VehicleRoutingSolution solution, PlanningVisit visit) {
        PlanningDepot depot = solution.getDepotList().get(0);
        return new DepotAngleVisitDifficultyWeight(
                visit,
                // angle of the line from visit to depot relative to visit→east
                visit.getLocation().angleTo(depot.getLocation()),
                visit.getLocation().distanceTo(depot.getLocation())
                        + depot.getLocation().distanceTo(visit.getLocation()));
    }

    static class DepotAngleVisitDifficultyWeight implements Comparable<DepotAngleVisitDifficultyWeight> {

        private static final Comparator<DepotAngleVisitDifficultyWeight> COMPARATOR =
                comparingDouble((DepotAngleVisitDifficultyWeight weight) -> weight.depotAngle)
                        // Ascending (further from the depot are more difficult)
                        .thenComparingLong(weight -> weight.depotRoundTripDistance)
                        .thenComparing(weight -> weight.visit, comparingLong(PlanningVisit::getId));

        private final PlanningVisit visit;
        private final double depotAngle;
        private final long depotRoundTripDistance;

        DepotAngleVisitDifficultyWeight(PlanningVisit visit, double depotAngle, long depotRoundTripDistance) {
            this.visit = visit;
            this.depotAngle = depotAngle;
            this.depotRoundTripDistance = depotRoundTripDistance;
        }

        @Override
        public int compareTo(DepotAngleVisitDifficultyWeight other) {
            return COMPARATOR.compare(this, other);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof DepotAngleVisitDifficultyWeight)) {
                return false;
            }
            return compareTo((DepotAngleVisitDifficultyWeight) o) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(visit, depotAngle, depotRoundTripDistance);
        }

        @Override
        public String toString() {
            return "DepotAngleVisitDifficultyWeight{" +
                    "visit=" + visit +
                    ", depotAngle=" + depotAngle +
                    ", depotRoundTripDistance=" + depotRoundTripDistance +
                    '}';
        }
    }
}
