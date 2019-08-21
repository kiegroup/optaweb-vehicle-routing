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

public class RemoveCustomer implements ProblemFactChange<VehicleRoutingSolution> {

    private final Customer removedCustomer;

    public RemoveCustomer(Customer removedCustomer) {
        this.removedCustomer = Objects.requireNonNull(removedCustomer);
    }

    @Override
    public void doChange(ScoreDirector<VehicleRoutingSolution> scoreDirector) {
        VehicleRoutingSolution workingSolution = scoreDirector.getWorkingSolution();

        // Look up a working copy of the customer
        Customer workingCustomer = scoreDirector.lookUpWorkingObject(removedCustomer);
        if (workingCustomer == null) {
            throw new IllegalStateException("Can't look up a working copy of " + removedCustomer);
        }

        // Fix the next customer and set its previousStandstill to the removed customer's previousStandstill
        for (Customer nextCustomer : workingSolution.getCustomerList()) {
            if (nextCustomer.getPreviousStandstill().equals(workingCustomer)) {
                scoreDirector.beforeVariableChanged(nextCustomer, "previousStandstill");
                nextCustomer.setPreviousStandstill(workingCustomer.getPreviousStandstill());
                scoreDirector.afterVariableChanged(nextCustomer, "previousStandstill");
                break;
            }
        }

        // Note: Unlike facts and fact collections, which are shared between best solutions and working solutions,
        // planning entities and collections are cloned during solving, so we don't need to clone customerList here.
        // To learn more about problem fact changes, see:
        // https://docs.jboss.org/optaplanner/release/latest/optaplanner-docs/html_single/#problemFactChangeExample

        // Remove the customer
        scoreDirector.beforeEntityRemoved(workingCustomer);
        if (!workingSolution.getCustomerList().remove(workingCustomer)) {
            throw new IllegalStateException(
                    "Working solution's customerList "
                            + workingSolution.getCustomerList()
                            + " doesn't contain the workingCustomer ("
                            + workingCustomer
                            + "). This is a bug!"
            );
        }
        scoreDirector.afterEntityRemoved(workingCustomer);

        scoreDirector.triggerVariableListeners();
    }
}
