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

package org.optaweb.vehiclerouting.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

/**
 * Route plan for the whole vehicle fleet.
 */
public class RoutingPlan {

    private static final RoutingPlan EMPTY = new RoutingPlan("", emptyList(), null, emptyList());

    private final String distance;
    private final List<Vehicle> vehicles;
    private final Location depot;
    private final List<RouteWithTrack> routes;

    /**
     * Create a routing plan.
     * @param distance the overall travel distance
     * @param vehicles all available vehicles
     * @param depot the depot (may be null)
     * @param routes routes of all vehicles
     */
    public RoutingPlan(String distance, List<Vehicle> vehicles, Location depot, List<RouteWithTrack> routes) {
        this.distance = Objects.requireNonNull(distance);
        this.vehicles = new ArrayList<>(Objects.requireNonNull(vehicles));
        this.depot = depot;
        this.routes = new ArrayList<>(Objects.requireNonNull(routes));
        if (depot == null) {
            if (!routes.isEmpty()) {
                throw new IllegalArgumentException("Routes must be empty when depot is null.");
            }
            // TODO fixme non-viable mutations
        } else if (routes.size() != vehicles.size()) {
            throw new IllegalArgumentException(describeVehiclesRoutesInconsistency(
                    "There must be exactly one route per vehicle", vehicles, routes
            ));
        } else if (haveDifferentVehicles(vehicles, routes)) {
            throw new IllegalArgumentException(describeVehiclesRoutesInconsistency(
                    "Some routes are assigned to non-existent vehicles", vehicles, routes
            ));
        }
    }

    private static boolean haveDifferentVehicles(List<Vehicle> vehicles, List<RouteWithTrack> routes) {
        return routes.stream()
                .map(Route::vehicle)
                .anyMatch(vehicle -> !vehicles.contains(vehicle));
    }

    private static String describeVehiclesRoutesInconsistency(
            String cause,
            List<Vehicle> vehicles,
            List<? extends Route> routes
    ) {
        List<Long> vehicleIdsFromRoutes = routes.stream()
                .map(route -> route.vehicle().id())
                .collect(Collectors.toList());
        return cause
                + ":\n- Vehicles (" + vehicles.size() + "): " + vehicles
                + "\n- Routes' vehicleIds (" + routes.size() + "): " + vehicleIdsFromRoutes;
    }

    /**
     * Create an empty routing plan.
     * @return empty routing plan
     */
    public static RoutingPlan empty() {
        return EMPTY;
    }

    /**
     * Overall travel distance.
     * @return travel distance
     */
    public String distance() {
        return distance;
    }

    /**
     * All available vehicles.
     * @return all vehicles
     */
    public List<Vehicle> vehicles() {
        return Collections.unmodifiableList(vehicles);
    }

    /**
     * Routes of all vehicles in the depot. Includes empty routes of vehicles that stay in the depot.
     * @return all routes (may be empty when there is no depot or no vehicles)
     */
    public List<RouteWithTrack> routes() {
        return Collections.unmodifiableList(routes);
    }

    /**
     * The depot.
     * @return depot (may be missing)
     */
    public Optional<Location> depot() {
        return Optional.ofNullable(depot);
    }
}
