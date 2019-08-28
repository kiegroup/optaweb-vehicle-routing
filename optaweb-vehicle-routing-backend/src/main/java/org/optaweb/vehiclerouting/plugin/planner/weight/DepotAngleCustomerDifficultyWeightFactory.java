/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaweb.vehiclerouting.plugin.planner.weight;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;
import org.optaweb.vehiclerouting.plugin.planner.VehicleRoutingSolution;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningDepot;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVisit;

public class DepotAngleCustomerDifficultyWeightFactory implements SelectionSorterWeightFactory<VehicleRoutingSolution
        , PlanningVisit> {
    public DepotAngleCustomerDifficultyWeightFactory() {
    }

    public DepotAngleCustomerDifficultyWeightFactory.DepotAngleCustomerDifficultyWeight createSorterWeight(
            VehicleRoutingSolution vehicleRoutingSolution, PlanningVisit customer) {
        PlanningDepot depot = (PlanningDepot) vehicleRoutingSolution.getDepotList().get(0);
        return new DepotAngleCustomerDifficultyWeightFactory.DepotAngleCustomerDifficultyWeight(customer,
                customer.getLocation().getAngle(depot.getLocation()),
                customer.getLocation().getDistanceTo(depot.getLocation())
                        + depot.getLocation().getDistanceTo(customer.getLocation()));
    }

    public static class DepotAngleCustomerDifficultyWeight implements
            Comparable<DepotAngleCustomerDifficultyWeightFactory.DepotAngleCustomerDifficultyWeight> {
        private final PlanningVisit customer;
        private final double depotAngle;
        private final long depotRoundTripDistance;

        public DepotAngleCustomerDifficultyWeight(PlanningVisit customer, double depotAngle,
                                                  long depotRoundTripDistance) {
            this.customer = customer;
            this.depotAngle = depotAngle;
            this.depotRoundTripDistance = depotRoundTripDistance;
        }

        public int compareTo(DepotAngleCustomerDifficultyWeightFactory.DepotAngleCustomerDifficultyWeight other) {
            return (new CompareToBuilder()).append(this.depotAngle, other.depotAngle).
                    append(this.depotRoundTripDistance, other.depotRoundTripDistance).append(this.customer.getId(),
                    other.customer.getId()).toComparison();
        }
    }

}
