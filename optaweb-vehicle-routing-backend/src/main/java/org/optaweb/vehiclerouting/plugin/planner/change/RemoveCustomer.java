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

import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.solver.ProblemFactChange;
import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.domain.location.Location;

public class RemoveCustomer implements ProblemFactChange<VehicleRoutingSolution> {

    private final Location location;

    public RemoveCustomer(Location location) {
        this.location = location;
    }

    @Override
    public void doChange(ScoreDirector<VehicleRoutingSolution> scoreDirector) {
        VehicleRoutingSolution workingSolution = scoreDirector.getWorkingSolution();
        Customer customer = workingSolution.getCustomerList().stream()
                .filter(v -> v.getLocation().getId().equals(location.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid request for removing customer at " + location));

        // Fix the next customer and set its previousStandstill to the removed customer's previousStandstill
        for (Customer nextCustomer : workingSolution.getCustomerList()) {
            if (nextCustomer.getPreviousStandstill().equals(customer)) {
                scoreDirector.beforeVariableChanged(nextCustomer, "previousStandstill");
                nextCustomer.setPreviousStandstill(customer.getPreviousStandstill());
                scoreDirector.afterVariableChanged(nextCustomer, "previousStandstill");
                break;
            }
        }

        // Remove the customer
        scoreDirector.beforeEntityRemoved(customer);
        if (!workingSolution.getCustomerList().remove(customer)) {
            throw new IllegalStateException("This is impossible.");
        }
        scoreDirector.afterEntityRemoved(customer);

        scoreDirector.triggerVariableListeners();
    }
}
