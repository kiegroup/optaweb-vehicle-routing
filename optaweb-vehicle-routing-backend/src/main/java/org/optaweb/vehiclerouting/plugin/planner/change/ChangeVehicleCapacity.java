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

package org.optaweb.vehiclerouting.plugin.planner.change;

import java.util.Objects;

import org.optaplanner.core.api.solver.change.ProblemChange;
import org.optaplanner.core.api.solver.change.ProblemChangeDirector;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVehicle;
import org.optaweb.vehiclerouting.plugin.planner.domain.VehicleRoutingSolution;

public class ChangeVehicleCapacity implements ProblemChange<VehicleRoutingSolution> {

    private final PlanningVehicle vehicle;

    public ChangeVehicleCapacity(PlanningVehicle vehicle) {
        this.vehicle = Objects.requireNonNull(vehicle);
    }

    @Override
    public void doChange(VehicleRoutingSolution workingSolution, ProblemChangeDirector problemChangeDirector) {
        // No need to clone the workingVehicle because it is a planning entity, so it is already planning-cloned.
        // To learn more about problem fact changes, see:
        // https://www.optaplanner.org/docs/optaplanner/latest/repeated-planning/repeated-planning.html#problemChangeExample
        problemChangeDirector.changeProblemProperty(vehicle,
                workingVehicle -> workingVehicle.setCapacity(vehicle.getCapacity()));
    }
}
