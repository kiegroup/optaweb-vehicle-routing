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

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;

@PlanningSolution
public class VehicleRoutingSolution {

    @ProblemFactCollectionProperty
    private List<PlanningDepot> depotList;
    @PlanningEntityCollectionProperty
    @ValueRangeProvider(id = "vehicleRange")
    private List<PlanningVehicle> vehicleList;
    @PlanningEntityCollectionProperty
    @ValueRangeProvider(id = "visitRange")
    private List<PlanningVisit> visitList;
    @PlanningScore
    private HardSoftLongScore score;

    VehicleRoutingSolution() {
        // Hide public constructor in favor of the factory.
    }

    public List<PlanningDepot> getDepotList() {
        return this.depotList;
    }

    public void setDepotList(List<PlanningDepot> depotList) {
        this.depotList = depotList;
    }

    public List<PlanningVehicle> getVehicleList() {
        return this.vehicleList;
    }

    public void setVehicleList(List<PlanningVehicle> vehicleList) {
        this.vehicleList = vehicleList;
    }

    public List<PlanningVisit> getVisitList() {
        return this.visitList;
    }

    public void setVisitList(List<PlanningVisit> visitList) {
        this.visitList = visitList;
    }

    public HardSoftLongScore getScore() {
        return this.score;
    }

    public void setScore(HardSoftLongScore score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "VehicleRoutingSolution{" +
                "depotList=" + depotList +
                ", vehicleList=" + vehicleList +
                ", visitList=" + visitList +
                ", score=" + score +
                '}';
    }
}
