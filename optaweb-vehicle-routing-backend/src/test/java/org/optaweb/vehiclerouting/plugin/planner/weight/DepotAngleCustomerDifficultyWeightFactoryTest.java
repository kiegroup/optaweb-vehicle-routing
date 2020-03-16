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

package org.optaweb.vehiclerouting.plugin.planner.weight;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningDepot;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningLocation;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVisitFactory;
import org.optaweb.vehiclerouting.plugin.planner.domain.SolutionFactory;
import org.optaweb.vehiclerouting.plugin.planner.domain.VehicleRoutingSolution;

import static org.assertj.core.api.Assertions.assertThat;

class DepotAngleCustomerDifficultyWeightFactoryTest {

    private final PlanningLocation location1 = new PlanningLocation(1, 1.0, 1.0);
    private final PlanningLocation location2 = new PlanningLocation(2, 1.0, 1.0);
    private final PlanningLocation location3 = new PlanningLocation(3, 1.0, 50.0);

    DepotAngleCustomerDifficultyWeightFactoryTest() {
        //Location 1 is close to location 2 and far away from location 3
        Map<PlanningLocation, Double> travelMap1 = new HashMap<>();
        travelMap1.put(location2, 100.0);
        travelMap1.put(location3, 10000.0);
        //Location 2 is close to location 1 and far away from location 3
        location1.setTravelDistanceMap(travelMap1);
        Map<PlanningLocation, Double> travelMap2 = new HashMap<>();
        travelMap2.put(location1, 100.0);
        travelMap2.put(location3, 10000.0);
        location2.setTravelDistanceMap(travelMap2);
        //Location 3 is far away form everything
        Map<PlanningLocation, Double> travelMap3 = new HashMap<>();
        travelMap3.put(location1, 10000.0);
        travelMap3.put(location3, 10000.0);
        location3.setTravelDistanceMap(travelMap3);
    }

    @Test
    void createSorterWeight_close_customer_should_have_smaller_weight() {
        VehicleRoutingSolution solution = SolutionFactory.emptySolution();
        solution.getDepotList().add(new PlanningDepot(location1));
        DepotAngleCustomerDifficultyWeightFactory weightFactory = new DepotAngleCustomerDifficultyWeightFactory();
        DepotAngleCustomerDifficultyWeightFactory.DepotAngleCustomerDifficultyWeight closeCustomerWeight =
                weightFactory.createSorterWeight(solution, PlanningVisitFactory.visit(location2));
        DepotAngleCustomerDifficultyWeightFactory.DepotAngleCustomerDifficultyWeight farCustomerWeight =
                weightFactory.createSorterWeight(solution, PlanningVisitFactory.visit(location3));
        assertThat(closeCustomerWeight).isLessThan(farCustomerWeight);
    }
}
