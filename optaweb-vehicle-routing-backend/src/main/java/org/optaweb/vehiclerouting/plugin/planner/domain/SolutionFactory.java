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

import java.util.ArrayList;
import java.util.List;

import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;

/**
 * Creates {@link VehicleRoutingSolution} instances.
 */
public class SolutionFactory {

    private SolutionFactory() {
        throw new AssertionError("Utility class");
    }

    /**
     * Create an empty solution. Empty solution has zero locations, depots, visits and vehicles and a zero score.
     *
     * @return empty solution
     */
    public static VehicleRoutingSolution emptySolution() {
        VehicleRoutingSolution solution = new VehicleRoutingSolution();
        solution.setVisitList(new ArrayList<>());
        solution.setDepotList(new ArrayList<>());
        solution.setVehicleList(new ArrayList<>());
        solution.setScore(HardSoftLongScore.ZERO);
        return solution;
    }

    /**
     * Create a new solution from given vehicles, depot and visits.
     * All vehicles will be placed in the depot.
     * <p>
     * The returned solution's vehicles and locations are new collections so modifying the solution
     * won't affect the collections given as arguments.
     * <p>
     * <strong><em>Elements of the argument collections are NOT cloned.</em></strong>
     *
     * @param vehicles vehicles
     * @param depot depot
     * @param visits visits
     * @return solution containing the given vehicles, depot, visits and their locations
     */
    public static VehicleRoutingSolution solutionFromVisits(
            List<PlanningVehicle> vehicles,
            PlanningDepot depot,
            List<PlanningVisit> visits) {
        VehicleRoutingSolution solution = new VehicleRoutingSolution();
        solution.setVehicleList(new ArrayList<>(vehicles));
        solution.setDepotList(new ArrayList<>(1));
        if (depot != null) {
            solution.getDepotList().add(depot);
            moveAllVehiclesToDepot(vehicles, depot);
        }
        solution.setVisitList(new ArrayList<>(visits));
        solution.setScore(HardSoftLongScore.ZERO);
        return solution;
    }

    private static void moveAllVehiclesToDepot(List<PlanningVehicle> vehicles, PlanningDepot depot) {
        vehicles.forEach(vehicle -> vehicle.setDepot(depot));
    }
}
