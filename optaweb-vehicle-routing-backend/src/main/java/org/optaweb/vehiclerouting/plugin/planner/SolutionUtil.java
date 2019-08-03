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
import org.optaplanner.examples.vehiclerouting.domain.location.RoadLocation;

/**
 * Provides common operations on solution that are not part of its API.
 */
public class SolutionUtil {

    public static final int DEFAULT_CUSTOMER_DEMAND = 1;
    static final int DEFAULT_VEHICLE_CAPACITY = 10;

    private SolutionUtil() {
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
     * Add vehicle with zero capacity.
     * @param solution solution
     * @param id vehicle id
     * @return the new vehicle
     */
    static Vehicle addVehicle(VehicleRoutingSolution solution, long id) {
        return addVehicle(solution, id, 0);
    }

    private static Vehicle addVehicle(VehicleRoutingSolution solution, long id, int capacity) {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(id);
        vehicle.setCapacity(capacity);
        solution.getVehicleList().add(vehicle);
        return vehicle;
    }

    /**
     * Translate domain vehicle to a planning vehicle.
     * @param domainVehicle domain vehicle
     * @return planning vehicle
     */
    static Vehicle planningVehicle(org.optaweb.vehiclerouting.domain.Vehicle domainVehicle) {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(domainVehicle.id());
        vehicle.setCapacity(DEFAULT_VEHICLE_CAPACITY);
        return vehicle;
    }

    /**
     * Translate domain location to a planning location.
     * @param location domain location
     * @return planning location
     */
    static RoadLocation planningLocation(org.optaweb.vehiclerouting.domain.Location location) {
        return new RoadLocation(
                location.id(),
                location.coordinates().latitude().doubleValue(),
                location.coordinates().longitude().doubleValue()
        );
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
    private static Customer addCustomer(VehicleRoutingSolution solution, Location location, int demand) {
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

    /**
     * Create a new solution from given vehicles, depot and visit. The returned solution's vehicles and locations
     * are new collections so modifying the solution won't affect the collections given as arguments.
     * <p>
     * <strong><em>Elements of the argument collections are NOT cloned.</em></strong>
     * @param vehicles vehicles
     * @param depot depot
     * @param visits visits
     * @return solution containing the given vehicles, depot, visits and their locations
     */
    static VehicleRoutingSolution createSolution(List<Vehicle> vehicles, Depot depot, List<RoadLocation> visits) {
        VehicleRoutingSolution solution = emptySolution();
        solution.getVehicleList().addAll(vehicles);
        if (depot != null) {
            solution.getLocationList().add(depot.getLocation());
            solution.getDepotList().add(depot);
            moveAllVehiclesTo(solution, depot);
        }
        solution.getLocationList().addAll(visits);
        solution.getCustomerList().addAll(visits.stream().map(SolutionUtil::customer).collect(Collectors.toList()));
        return solution;
    }

    private static Customer customer(RoadLocation location) {
        Customer customer = new Customer();
        customer.setId(location.getId());
        customer.setLocation(location);
        customer.setDemand(DEFAULT_CUSTOMER_DEMAND);
        return customer;
    }
}
