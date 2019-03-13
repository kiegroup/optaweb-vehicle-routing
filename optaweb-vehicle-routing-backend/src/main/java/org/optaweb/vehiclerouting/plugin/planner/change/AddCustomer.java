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
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.domain.location.Location;
import org.optaweb.vehiclerouting.plugin.planner.SolutionUtil;

public class AddCustomer implements ProblemFactChange<VehicleRoutingSolution> {

    private final Location location;

    public AddCustomer(Location location) {
        this.location = Objects.requireNonNull(location);
    }

    @Override
    public void doChange(ScoreDirector<VehicleRoutingSolution> scoreDirector) {
        VehicleRoutingSolution workingSolution = scoreDirector.getWorkingSolution();
        workingSolution.setLocationList(new ArrayList<>(workingSolution.getLocationList()));

        scoreDirector.beforeProblemFactAdded(location);
        workingSolution.getLocationList().add(location);
        scoreDirector.afterProblemFactAdded(location);

        Customer customer = new Customer();
        customer.setId(location.getId());
        customer.setLocation(location);
        customer.setDemand(SolutionUtil.DEFAULT_CUSTOMER_DEMAND);

        scoreDirector.beforeEntityAdded(customer);
        workingSolution.getCustomerList().add(customer);
        scoreDirector.afterEntityAdded(customer);

        scoreDirector.triggerVariableListeners();
    }
}
