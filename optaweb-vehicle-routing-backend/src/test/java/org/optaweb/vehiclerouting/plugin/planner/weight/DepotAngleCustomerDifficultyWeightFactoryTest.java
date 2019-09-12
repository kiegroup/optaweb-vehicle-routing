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
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVehicle;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVisit;
import org.optaweb.vehiclerouting.plugin.planner.domain.SolutionFactory;
import org.optaweb.vehiclerouting.plugin.planner.domain.Standstill;
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
        VehicleRoutingSolution solution = createSolution(location1, location2, location3);
        DepotAngleCustomerDifficultyWeightFactory weightFactory = new DepotAngleCustomerDifficultyWeightFactory();
        DepotAngleCustomerDifficultyWeightFactory.DepotAngleCustomerDifficultyWeight closeCustomerWeight =
                weightFactory.createSorterWeight(solution, solution.getVisitList().get(0));
        DepotAngleCustomerDifficultyWeightFactory.DepotAngleCustomerDifficultyWeight farCustomerWeight =
                weightFactory.createSorterWeight(solution, solution.getVisitList().get(1));
        assertThat(closeCustomerWeight).isLessThan(farCustomerWeight);
    }

    /**
     * Create a solution with 1 vehicle with depot being the first location and visiting all customers specified by
     * the rest of locations.
     * @param locations depot and visit locations
     * @return initialized solution
     */
    private static VehicleRoutingSolution createSolution(PlanningLocation... locations) {
        VehicleRoutingSolution solution = SolutionFactory.emptySolution();

        PlanningDepot depot = addDepot(solution, locations[0]);
        addVehicle(solution, 1);
        moveAllVehiclesTo(solution, depot);

        // create customers
        for (int i = 1; i < locations.length; i++) {
            addVisit(solution, locations[i]);
        }
        // visit all customers
        Standstill previousStandstill = solution.getVehicleList().get(0);
        for (PlanningVisit visit : solution.getVisitList()) {
            visit.setPreviousStandstill(previousStandstill);
            previousStandstill.setNextVisit(visit);
            previousStandstill = visit;
            visit.setVehicle(solution.getVehicleList().get(0));
        }
        return solution;
    }

    /**
     * Add depot.
     * @param solution solution
     * @param location depot's location
     * @return the new depot
     */
    private static PlanningDepot addDepot(VehicleRoutingSolution solution, PlanningLocation location) {
        PlanningDepot depot = new PlanningDepot();
        depot.setId(location.getId());
        depot.setLocation(location);
        solution.getDepotList().add(depot);
        solution.getLocationList().add(location);
        return depot;
    }

    /**
     * Add customer with demand.
     * @param solution solution
     * @param location customer's location
     */
    private static void addVisit(VehicleRoutingSolution solution, PlanningLocation location) {
        PlanningVisit visit = new PlanningVisit();
        visit.setId(location.getId());
        visit.setLocation(location);
        visit.setDemand(1);
        solution.getVisitList().add(visit);
        solution.getLocationList().add(location);
    }

    /**
     * Add vehicle with zero capacity.
     * @param solution solution
     * @param id vehicle id
     */
    private static void addVehicle(VehicleRoutingSolution solution, long id) {
        addVehicle(solution, id, 0);
    }

    private static void addVehicle(VehicleRoutingSolution solution, long id, int capacity) {
        PlanningVehicle vehicle = new PlanningVehicle();
        vehicle.setId(id);
        vehicle.setCapacity(capacity);
        solution.getVehicleList().add(vehicle);
    }

    /**
     * Move all vehicles to the specified depot.
     * @param solution solution
     * @param depot new vehicles' depot. May be null.
     */
    static void moveAllVehiclesTo(VehicleRoutingSolution solution, PlanningDepot depot) {
        solution.getVehicleList().forEach(vehicle -> vehicle.setDepot(depot));
    }
}
