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

package org.optaweb.vehiclerouting.solver.weight;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.event.BestSolutionChangedEvent;
import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.Customer;
import org.optaweb.vehiclerouting.domain.Depot;
import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.domain.Standstill;
import org.optaweb.vehiclerouting.plugin.planner.SolutionUtil;
import org.optaweb.vehiclerouting.service.location.DistanceMatrix;
import org.optaweb.vehiclerouting.service.route.RouteChangedEvent;
import org.optaweb.vehiclerouting.solver.VehicleRoutingSolution;
import org.optaweb.vehiclerouting.solver.score.VehicleRoutingEasyScoreCalculator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.task.AsyncTaskExecutor;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class DepotAngleCustomerDifficultyWeightFactoryTest {

    private final Location location1 = new Location(1, Coordinates.valueOf(1.0, 1.0));
    private final Location location2 = new Location(2, Coordinates.valueOf(1.0, 1.0));
    private final Location location3 = new Location(3, Coordinates.valueOf(1.0, 50.0));

    @InjectMocks
    private DepotAngleCustomerDifficultyWeightFactory  weightFactory;


    @Test
    void createSorterWeight_close_customer_should_have_smaller_weight() {
        VehicleRoutingSolution solution = createSolution(location1, location2, location3);
        DepotAngleCustomerDifficultyWeightFactory.DepotAngleCustomerDifficultyWeight closeCustomerWeight =
                weightFactory.createSorterWeight(solution,
                solution.getCustomerList().get(0));
        DepotAngleCustomerDifficultyWeightFactory.DepotAngleCustomerDifficultyWeight farCustomerWeight =
                weightFactory.createSorterWeight(solution,
                    solution.getCustomerList().get(1));
        assert(closeCustomerWeight.compareTo(farCustomerWeight) < 0);

    }
    @BeforeEach
    void setUp() {
    }



    /**
     * Create a solution with 1 vehicle with depot being the first location and visiting all customers specified by
     * the rest of locations.
     *
     * @param locations depot and customer locations
     * @return initialized solution
     */
    private static VehicleRoutingSolution createSolution(Location... locations) {
        VehicleRoutingSolution solution = SolutionUtil.emptySolution();

        Depot depot = SolutionUtil.addDepot(solution, locations[0]);
        SolutionUtil.addVehicle(solution, 1);
        SolutionUtil.moveAllVehiclesTo(solution, depot);

        // create customers
        for (int i = 1; i < locations.length; i++) {
            SolutionUtil.addCustomer(solution, locations[i]);
        }
        // visit all customers
        Standstill previousStandstill = solution.getVehicleList().get(0);
        for (Customer customer : solution.getCustomerList()) {
            customer.setPreviousStandstill(previousStandstill);
            previousStandstill.setNextCustomer(customer);
            previousStandstill = customer;
            customer.setVehicle(solution.getVehicleList().get(0));
        }
        return solution;
    }

    public DepotAngleCustomerDifficultyWeightFactoryTest() {
        //Location 1 is close to location 2 and far away from location 3
        Map<Location, Double> travelMap1 = new HashMap<>();
        travelMap1.put(location2, 100.0);
        travelMap1.put(location3, 10000.0);
        //Location 2 is close to location 1 and far away from location 3
        location1.setTravelDistanceMap(travelMap1);
        Map<Location, Double> travelMap2 = new HashMap<>();
        travelMap2.put(location1, 100.0);
        travelMap2.put(location3, 10000.0);
        location2.setTravelDistanceMap(travelMap2);
        //Location 3 is far away form everything
        Map<Location, Double> travelMap3 = new HashMap<>();
        travelMap3.put(location1, 10000.0);
        travelMap3.put(location3, 10000.0);
        location3.setTravelDistanceMap(travelMap3);
    }
}
