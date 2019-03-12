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
import java.util.Arrays;
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

    public static VehicleRoutingSolution emptySolution() {
        VehicleRoutingSolution solution = new VehicleRoutingSolution();
        solution.setLocationList(new ArrayList<>());
        solution.setCustomerList(new ArrayList<>());
        solution.setDepotList(new ArrayList<>());
        solution.setVehicleList(Arrays.asList(new Vehicle(), new Vehicle()));
        solution.getVehicleList().get(0).setId(1L);
        solution.getVehicleList().get(1).setId(2L);
        solution.setScore(HardSoftLongScore.ZERO);
        return solution;
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
}
