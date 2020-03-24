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

import java.text.NumberFormat;
import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;

@PlanningSolution
public class VehicleRoutingSolution {

    protected String name;
    private String distanceUnitOfMeasurement;
    private List<PlanningLocation> locationList;
    private List<PlanningDepot> depotList;
    private List<PlanningVehicle> vehicleList;
    private List<PlanningVisit> visitList;
    private HardSoftLongScore score;

    public VehicleRoutingSolution() {
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDistanceUnitOfMeasurement() {
        return this.distanceUnitOfMeasurement;
    }

    public void setDistanceUnitOfMeasurement(String distanceUnitOfMeasurement) {
        this.distanceUnitOfMeasurement = distanceUnitOfMeasurement;
    }

    @ProblemFactCollectionProperty
    public List<PlanningLocation> getLocationList() {
        return this.locationList;
    }

    public void setLocationList(List<PlanningLocation> locationList) {
        this.locationList = locationList;
    }

    @ProblemFactCollectionProperty
    public List<PlanningDepot> getDepotList() {
        return this.depotList;
    }

    public void setDepotList(List<PlanningDepot> depotList) {
        this.depotList = depotList;
    }

    @PlanningEntityCollectionProperty
    @ValueRangeProvider(
            id = "vehicleRange"
    )
    public List<PlanningVehicle> getVehicleList() {
        return this.vehicleList;
    }

    public void setVehicleList(List<PlanningVehicle> vehicleList) {
        this.vehicleList = vehicleList;
    }

    @PlanningEntityCollectionProperty
    @ValueRangeProvider(
            id = "visitRange"
    )
    public List<PlanningVisit> getVisitList() {
        return this.visitList;
    }

    public void setVisitList(List<PlanningVisit> visitList) {
        this.visitList = visitList;
    }

    @PlanningScore
    public HardSoftLongScore getScore() {
        return this.score;
    }

    public void setScore(HardSoftLongScore score) {
        this.score = score;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public String getDistanceString(NumberFormat numberFormat) {
        if (score == null) {
            return null;
        }
        long distance = -score.getSoftScore();
        if (distanceUnitOfMeasurement == null) {
            return numberFormat.format(((double) distance) / 1000.0);
        }
        switch (distanceUnitOfMeasurement) {
            case "sec":  // TODO why are the values 1000 larger?
                long hours = distance / 3600000L;
                long minutes = distance % 3600000L / 60000L;
                long seconds = distance % 60000L / 1000L;
                long milliseconds = distance % 1000L;
                return hours + "h " + minutes + "m " + seconds + "s " + milliseconds + "ms";
            case "meter": { // TODO why are the values 1000 larger?
                long km = distance / 1000L;
                long meter = distance % 1000L;
                return km + "km " + meter + "m";
            }
            default:
                return numberFormat.format(((double) distance) / 1000.0) + " " + distanceUnitOfMeasurement;
        }
    }

    @Override
    public String toString() {
        return "VehicleRoutingSolution{" +
                "name='" + name + '\'' +
                ", distanceUnitOfMeasurement='" + distanceUnitOfMeasurement + '\'' +
                ", locationList=" + locationList +
                ", depotList=" + depotList +
                ", vehicleList=" + vehicleList +
                ", visitList=" + visitList +
                ", score=" + score +
                '}';
    }
}
