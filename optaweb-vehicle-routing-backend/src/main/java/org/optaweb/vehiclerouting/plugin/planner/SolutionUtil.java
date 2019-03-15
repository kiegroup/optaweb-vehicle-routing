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
import java.util.Collections;
import java.util.List;

import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.Depot;
import org.optaplanner.examples.vehiclerouting.domain.Vehicle;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.domain.location.Location;
import org.optaplanner.examples.vehiclerouting.domain.location.RoadLocation;
import org.optaweb.vehiclerouting.domain.LatLng;
import org.optaweb.vehiclerouting.domain.Route;

/**
 * Provides common operations on solution that are not part of its API.
 */
public class SolutionUtil {

    public static final int DEFAULT_CUSTOMER_DEMAND = 1;
    public static final int DEFAULT_VEHICLE_CAPACITY = 10;

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
        addVehicle(solution, 1, DEFAULT_VEHICLE_CAPACITY);
        addVehicle(solution, 2, DEFAULT_VEHICLE_CAPACITY);
        addVehicle(solution, 3, DEFAULT_VEHICLE_CAPACITY);
        addVehicle(solution, 4, DEFAULT_VEHICLE_CAPACITY);
        addVehicle(solution, 5, DEFAULT_VEHICLE_CAPACITY);
        addVehicle(solution, 6, DEFAULT_VEHICLE_CAPACITY);
        return solution;
    }

    /**
     * Add vehicle with zero capacity.
     * @param solution solution
     * @param id vehicle id
     */
    static void addVehicle(VehicleRoutingSolution solution, long id) {
        addVehicle(solution, id, 0);
    }

    private static void addVehicle(VehicleRoutingSolution solution, long id, int capacity) {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(id);
        vehicle.setCapacity(capacity);
        solution.getVehicleList().add(vehicle);
    }

    /**
     * Extract routes from the solution. Includes empty routes of vehicles that stay in the depot.
     * @param solution solution
     * @return one route per vehicle
     */
    static List<Route> routes(VehicleRoutingSolution solution) {
        // TODO include unconnected customers in the result
        if (solution.getDepotList().isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<Route> routes = new ArrayList<>();
        for (Vehicle vehicle : solution.getVehicleList()) {
            Depot depot = vehicle.getDepot();
            if (depot == null) {
                throw new IllegalStateException("Vehicle (id=" + vehicle.getId() + ") is not in the depot. That's not allowed");
            }
            List<org.optaweb.vehiclerouting.domain.Location> visits = new ArrayList<>();
            for (Customer customer = vehicle.getNextCustomer(); customer != null; customer = customer.getNextCustomer()) {
                addLocationToRoute(visits, customer.getLocation());
            }
            routes.add(new Route(domainLocation(depot.getLocation()), visits));
        }
        return routes;
    }

    private static org.optaweb.vehiclerouting.domain.Location domainLocation(Location location) {
        return new org.optaweb.vehiclerouting.domain.Location(
                location.getId(),
                LatLng.valueOf(location.getLatitude(), location.getLongitude())
        );
    }

    /**
     * Translate domain location to a planning location.
     * @param location domain location
     * @return planning location
     */
    static RoadLocation planningLocation(org.optaweb.vehiclerouting.domain.Location location) {
        return new RoadLocation(
                location.getId(),
                location.getLatLng().getLatitude().doubleValue(),
                location.getLatLng().getLongitude().doubleValue()
        );
    }

    private static void addLocationToRoute(List<org.optaweb.vehiclerouting.domain.Location> route, Location location) {
        route.add(domainLocation(location));
    }

    /**
     * Get solution's depot.
     * @param solution the solution in which to look for the depot
     * @return first depot from the solution or null if there are no depots
     */
    static org.optaweb.vehiclerouting.domain.Location depot(VehicleRoutingSolution solution) {
        return solution.getDepotList().size() > 0 ? domainLocation(solution.getDepotList().get(0).getLocation()) : null;
    }

    /**
     * Add depot.
     * @param solution solution
     * @param location depot's location
     * @return the new depot
     */
    static Depot addDepot(VehicleRoutingSolution solution, Location location) {
        Depot depot = new Depot();
        depot.setId(location.getId());
        depot.setLocation(location);
        solution.getDepotList().add(depot);
        solution.getLocationList().add(location);
        return depot;
    }

    /**
     * Add customer with zero demand.
     * @param solution solution
     * @param location customer's location
     * @return the new customer
     */
    static Customer addCustomer(VehicleRoutingSolution solution, Location location) {
        return addCustomer(solution, location, 0);
    }

    /**
     * Add customer with demand.
     * @param solution solution
     * @param location customer's location
     * @param demand customer's demand
     * @return the new customer
     */
    static Customer addCustomer(VehicleRoutingSolution solution, Location location, int demand) {
        Customer customer = new Customer();
        customer.setId(location.getId());
        customer.setLocation(location);
        customer.setDemand(demand);
        solution.getCustomerList().add(customer);
        solution.getLocationList().add(location);
        return customer;
    }

    /**
     * Move all vehicles to the specified depot.
     * @param solution solution
     * @param depot new vehicles' depot. May be null.
     */
    static void moveAllVehiclesTo(VehicleRoutingSolution solution, Depot depot) {
        solution.getVehicleList().forEach(vehicle -> vehicle.setDepot(depot));
    }
}
