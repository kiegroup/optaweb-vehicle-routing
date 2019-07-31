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

import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.solver.ProblemFactChange;
import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.domain.location.Location;

public class RemoveCustomer implements ProblemFactChange<VehicleRoutingSolution> {

    private final Location removedLocation;

    public RemoveCustomer(Location removedLocation) {
        this.removedLocation = Objects.requireNonNull(removedLocation);
    }

    @Override
    public void doChange(ScoreDirector<VehicleRoutingSolution> scoreDirector) {
        VehicleRoutingSolution workingSolution = scoreDirector.getWorkingSolution();
        Customer removedCustomer = workingSolution.getCustomerList().stream()
                .filter(customer -> customer.getLocation().getId().equals(removedLocation.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid request for removing customer at " + removedLocation));

        // Fix the next customer and set its previousStandstill to the removed customer's previousStandstill
        for (Customer nextCustomer : workingSolution.getCustomerList()) {
            if (nextCustomer.getPreviousStandstill().equals(removedCustomer)) {
                scoreDirector.beforeVariableChanged(nextCustomer, "previousStandstill");
                nextCustomer.setPreviousStandstill(removedCustomer.getPreviousStandstill());
                scoreDirector.afterVariableChanged(nextCustomer, "previousStandstill");
                break;
            }
        }

        // Remove the customer
        scoreDirector.beforeEntityRemoved(removedCustomer);
        if (!workingSolution.getCustomerList().remove(removedCustomer)) {
            throw new IllegalStateException("This is impossible.");
        }
        scoreDirector.afterEntityRemoved(removedCustomer);

        scoreDirector.triggerVariableListeners();
    }
}
