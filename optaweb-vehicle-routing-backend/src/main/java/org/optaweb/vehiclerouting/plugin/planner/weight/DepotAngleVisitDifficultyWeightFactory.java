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

package org.optaweb.vehiclerouting.plugin.planner.weight;

import java.util.Comparator;
import java.util.Objects;

import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningDepot;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningLocation;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVisit;
import org.optaweb.vehiclerouting.plugin.planner.domain.VehicleRoutingSolution;

import static java.util.Comparator.comparingDouble;
import static java.util.Comparator.comparingLong;

/**
 * On large data sets, the constructed solution looks like pizza slices.
 * The order of the slices depends on the {@link PlanningLocation#getAngle} implementation.
 */
public class DepotAngleVisitDifficultyWeightFactory
        implements SelectionSorterWeightFactory<VehicleRoutingSolution, PlanningVisit> {

    @Override
    public DepotAngleVisitDifficultyWeight createSorterWeight(VehicleRoutingSolution solution, PlanningVisit visit) {
        PlanningDepot depot = solution.getDepotList().get(0);
        return new DepotAngleVisitDifficultyWeight(
                visit,
                // angle of the line from visit to depot relative to visit→east
                visit.getLocation().getAngle(depot.getLocation()),
                visit.getLocation().getDistanceTo(depot.getLocation())
                        + depot.getLocation().getDistanceTo(visit.getLocation())
        );
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
            return "DepotAngleCustomerDifficultyWeight{" +
                    "visit=" + visit +
                    ", depotAngle=" + depotAngle +
                    ", depotRoundTripDistance=" + depotRoundTripDistance +
                    '}';
        }
    }
}
