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

import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.Depot;
import org.optaplanner.examples.vehiclerouting.domain.Vehicle;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.domain.location.Location;
import org.optaweb.vehiclerouting.domain.LatLng;
import org.optaweb.vehiclerouting.domain.Route;

public class SolutionUtil {

    private SolutionUtil() {
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
        return solution;
    }

    /**
     * Create an initial solution with no locations and a single vehicle.
     * @return initial solution
     */
    static VehicleRoutingSolution initialSolution() {
        VehicleRoutingSolution solution = emptySolution();
        addVehicle(solution, 1);
        addVehicle(solution, 2);
        return solution;
    }

    static void addVehicle(VehicleRoutingSolution solution, long id) {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(id);
        solution.getVehicleList().add(vehicle);
    }

    static List<Route> routes(VehicleRoutingSolution solution) {
        // TODO include unconnected customers in the result
        ArrayList<Route> routes = new ArrayList<>();
        for (Vehicle vehicle : solution.getVehicleList()) {
            Depot depot = vehicle.getDepot();
            if (depot == null) {
                break;
            }
            List<org.optaweb.vehiclerouting.domain.Location> visits = new ArrayList<>();
            addLocationToRoute(visits, depot.getLocation());
            for (Customer customer = vehicle.getNextCustomer(); customer != null; customer = customer.getNextCustomer()) {
                addLocationToRoute(visits, customer.getLocation());
            }
            routes.add(new Route(visits));
        }
        return routes;
    }

    private static void addLocationToRoute(List<org.optaweb.vehiclerouting.domain.Location> route, Location location) {
        route.add(new org.optaweb.vehiclerouting.domain.Location(
                location.getId(),
                LatLng.valueOf(location.getLatitude(), location.getLongitude())
        ));
    }

    static Depot addDepot(VehicleRoutingSolution solution, Location location) {
        Depot depot = new Depot();
        depot.setId(location.getId());
        depot.setLocation(location);
        solution.getDepotList().add(depot);
        solution.getLocationList().add(location);
        return depot;
    }

    static Customer addCustomer(VehicleRoutingSolution solution, Location location) {
        Customer customer = new Customer();
        customer.setId(location.getId());
        customer.setLocation(location);
        solution.getCustomerList().add(customer);
        solution.getLocationList().add(location);
        return customer;
    }

    static void moveAllVehiclesTo(VehicleRoutingSolution solution, Depot depot) {
        solution.getVehicleList().forEach(vehicle -> vehicle.setDepot(depot));
    }
}
