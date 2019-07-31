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

import java.util.ArrayList;
import java.util.Objects;

import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.solver.ProblemFactChange;
import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.Vehicle;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;

public class RemoveVehicle implements ProblemFactChange<VehicleRoutingSolution> {

    private final Vehicle removedVehicle;

    public RemoveVehicle(Vehicle removedVehicle) {
        this.removedVehicle = Objects.requireNonNull(removedVehicle);
    }

    @Override
    public void doChange(ScoreDirector<VehicleRoutingSolution> scoreDirector) {
        VehicleRoutingSolution workingSolution = scoreDirector.getWorkingSolution();

        // look up working copy of the vehicle
        Vehicle workingVehicle = scoreDirector.lookUpWorkingObject(removedVehicle);
        if (workingVehicle == null) {
            throw new IllegalStateException("Can't look up working copy of " + removedVehicle);
        }

        // Un-initialize all customer visited by this vehicle
        Customer visitedCustomer = workingVehicle.getNextCustomer();
        while (visitedCustomer != null) {
            scoreDirector.beforeVariableChanged(visitedCustomer, "previousStandstill");
            visitedCustomer.setPreviousStandstill(null);
            scoreDirector.afterVariableChanged(visitedCustomer, "previousStandstill");

            visitedCustomer = visitedCustomer.getNextCustomer();
        }

        // shallow clone fact list
        workingSolution.setVehicleList(new ArrayList<>(workingSolution.getVehicleList()));
        // Remove the vehicle
        scoreDirector.beforeProblemFactRemoved(workingVehicle);
        if (!workingSolution.getVehicleList().remove(workingVehicle)) {
            throw new IllegalStateException("This is a bug.");
        }
        scoreDirector.afterProblemFactRemoved(workingVehicle);

        scoreDirector.triggerVariableListeners();
    }
}
