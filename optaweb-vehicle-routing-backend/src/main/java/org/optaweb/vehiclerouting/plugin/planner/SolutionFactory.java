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

package org.optaweb.vehiclerouting.plugin.planner;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.Depot;
import org.optaplanner.examples.vehiclerouting.domain.Vehicle;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.domain.location.Location;

/**
 * Creates {@link VehicleRoutingSolution} instances.
 */
public class SolutionFactory {

    private SolutionFactory() {
        throw new AssertionError("Utility class");
    }

    /**
     * Create an empty solution. Empty solution has zero locations, depots, customers and vehicles and a zero score.
     * @return empty solution
     */
    public static VehicleRoutingSolution emptySolution() {
        VehicleRoutingSolution solution = new VehicleRoutingSolution();
        solution.setLocationList(new ArrayList<>());
        solution.setCustomerList(new ArrayList<>());
        solution.setDepotList(new ArrayList<>());
        solution.setVehicleList(new ArrayList<>());
        solution.setScore(HardSoftLongScore.ZERO);
        solution.setDistanceUnitOfMeasurement("sec");
        return solution;
    }

    /**
     * Create a new solution from given vehicles, depot and customer locations.
     * This will create a customer for each given customer location. All vehicles will be placed in the depot.
     * <p>
     * The returned solution's vehicles and locations are new collections so modifying the solution
     * won't affect the collections given as arguments.
     * <p>
     * <strong><em>Elements of the argument collections are NOT cloned.</em></strong>
     * @param vehicles vehicles
     * @param depot depot (may be {@code null})
     * @param customerLocations customer locations
     * @return solution containing the given vehicles, depot, visits and their locations
     */
    static VehicleRoutingSolution solutionFromLocations(
            List<Vehicle> vehicles,
            Depot depot,
            List<? extends Location> customerLocations
    ) {
        return solution(
                vehicles,
                depot,
                customerLocations.stream().map(CustomerFactory::customer).collect(Collectors.toList()),
                customerLocations
        );
    }

    /**
     * Create a new solution from given vehicles, depot and customers.
     * All vehicles will be placed in the depot.
     * <p>
     * The returned solution's vehicles and locations are new collections so modifying the solution
     * won't affect the collections given as arguments.
     * <p>
     * <strong><em>Elements of the argument collections are NOT cloned.</em></strong>
     * @param vehicles vehicles
     * @param depot depot
     * @param customers customers
     * @return solution containing the given vehicles, depot, visits and their locations
     */
    static VehicleRoutingSolution solutionFromCustomers(
            List<Vehicle> vehicles,
            Depot depot,
            List<Customer> customers
    ) {
        return solution(
                vehicles,
                depot,
                customers,
                customers.stream().map(Customer::getLocation).collect(Collectors.toList())
        );
    }

    private static VehicleRoutingSolution solution(
            List<Vehicle> vehicles,
            Depot depot,
            List<Customer> customers,
            List<? extends Location> visitLocations
    ) {
        VehicleRoutingSolution solution = new VehicleRoutingSolution();
        solution.setVehicleList(new ArrayList<>(vehicles));
        solution.setLocationList(new ArrayList<>(visitLocations));
        solution.setDepotList(new ArrayList<>(1));
        if (depot != null) {
            solution.getLocationList().add(depot.getLocation());
            solution.getDepotList().add(depot);
            moveAllVehiclesToDepot(vehicles, depot);
        }
        solution.setCustomerList(new ArrayList<>(customers));
        solution.setScore(HardSoftLongScore.ZERO);
        solution.setDistanceUnitOfMeasurement("sec");
        return solution;
    }

    private static void moveAllVehiclesToDepot(List<Vehicle> vehicles, Depot depot) {
        vehicles.forEach(vehicle -> vehicle.setDepot(depot));
    }
}
