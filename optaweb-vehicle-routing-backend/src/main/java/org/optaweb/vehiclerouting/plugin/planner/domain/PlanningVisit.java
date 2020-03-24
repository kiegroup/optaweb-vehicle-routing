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

package org.optaweb.vehiclerouting.plugin.planner.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.AnchorShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableGraphType;
import org.optaweb.vehiclerouting.plugin.planner.weight.DepotAngleVisitDifficultyWeightFactory;

@PlanningEntity(difficultyWeightFactoryClass = DepotAngleVisitDifficultyWeightFactory.class)
public class PlanningVisit extends AbstractPlanningObject implements Standstill {

    protected PlanningLocation location;
    protected int demand;

    // Planning variables: changes during planning, between score calculations.
    protected Standstill previousStandstill;

    // Shadow variables
    protected PlanningVisit nextVisit;
    protected PlanningVehicle vehicle;

    @Override
    public PlanningLocation getLocation() {
        return location;
    }

    public void setLocation(PlanningLocation location) {
        this.location = location;
    }

    public int getDemand() {
        return demand;
    }

    public void setDemand(int demand) {
        this.demand = demand;
    }

    @PlanningVariable(valueRangeProviderRefs = {"vehicleRange", "visitRange"},
            graphType = PlanningVariableGraphType.CHAINED)
    public Standstill getPreviousStandstill() {
        return previousStandstill;
    }

    public void setPreviousStandstill(Standstill previousStandstill) {
        this.previousStandstill = previousStandstill;
    }

    @Override
    public PlanningVisit getNextVisit() {
        return nextVisit;
    }

    @Override
    public void setNextVisit(PlanningVisit nextVisit) {
        this.nextVisit = nextVisit;
    }

    @Override
    @AnchorShadowVariable(sourceVariableName = "previousStandstill")
    public PlanningVehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(PlanningVehicle vehicle) {
        this.vehicle = vehicle;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    /**
     * @return a positive number, the distance multiplied by 1000 to avoid floating point arithmetic rounding errors
     */
    public long getDistanceFromPreviousStandstill() {
        if (previousStandstill == null) {
            throw new IllegalStateException(
                    "This method must not be called when the previousStandstill ("
                            + previousStandstill + ") is not initialized yet."
            );
        }
        return getDistanceFrom(previousStandstill);
    }

    /**
     * @param standstill never null
     * @return a positive number, the distance multiplied by 1000 to avoid floating point arithmetic rounding errors
     */
    public long getDistanceFrom(Standstill standstill) {
        return standstill.getLocation().getDistanceTo(location);
    }

    /**
     * @param standstill never null
     * @return a positive number, the distance multiplied by 1000 to avoid floating point arithmetic rounding errors
     */
    public long getDistanceTo(Standstill standstill) {
        return location.getDistanceTo(standstill.getLocation());
    }

    @Override
    public String toString() {
        return "PlanningVisit{" +
                (location == null ? "" : "location=" + location.getId()) +
                ",demand=" + demand +
                (previousStandstill == null ? "" : ",previousStandstill='" + previousStandstill.getLocation().getId()) +
                (nextVisit == null ? "" : ",nextVisit=" + nextVisit.getId()) +
                (vehicle == null ? "" : ",vehicle=" + vehicle.getId()) +
                ",id=" + id +
                '}';
    }
}
