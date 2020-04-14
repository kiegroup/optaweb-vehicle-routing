/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaweb.vehiclerouting.plugin.planner;

import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVisit;

import static java.util.function.Function.identity;
import static org.optaplanner.core.api.score.stream.ConstraintCollectors.sum;
import static org.optaplanner.core.api.score.stream.Joiners.equal;

public class VehicleRoutingConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
                vehicleCapacity(constraintFactory),
                distanceFromPreviousStandstill(constraintFactory),
                distanceFromLastVisitToDepot(constraintFactory)
        };
    }

    Constraint vehicleCapacity(ConstraintFactory constraintFactory) {
        return constraintFactory.from(PlanningVisit.class)
                .groupBy(PlanningVisit::getVehicle, sum(PlanningVisit::getDemand))
                .filter((vehicle, demand) -> demand > vehicle.getCapacity())
                .penalizeLong(
                        "vehicle capacity",
                        HardSoftLongScore.ONE_HARD,
                        (vehicle, demand) -> demand - vehicle.getCapacity()
                );
    }

    Constraint distanceFromPreviousStandstill(ConstraintFactory constraintFactory) {
        return constraintFactory.from(PlanningVisit.class)
                .penalizeLong(
                        "distance from previous standstill",
                        HardSoftLongScore.ONE_SOFT,
                        PlanningVisit::getDistanceFromPreviousStandstill
                );
    }

    Constraint distanceFromLastVisitToDepot(ConstraintFactory constraintFactory) {
        return constraintFactory.from(PlanningVisit.class)
                // There is no visit, such that its identity is the next visit of the visit at the start of the stream.
                .ifNotExists(PlanningVisit.class, equal(PlanningVisit::getNextVisit, identity()))
                .penalizeLong(
                        "distance from last visit to depot",
                        HardSoftLongScore.ONE_SOFT,
                        PlanningVisit::getDistanceToDepot
                );
    }
}
