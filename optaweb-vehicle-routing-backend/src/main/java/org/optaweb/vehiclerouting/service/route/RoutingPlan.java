/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.optaweb.vehiclerouting.service.route;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.domain.RouteWithTrack;

/**
 * Route plan for the whole vehicle fleet.
 */
public class RoutingPlan {

    private final String distance;
    private final Location depot;
    private final List<RouteWithTrack> routes;

    /**
     * Create a routing plan.
     * @param distance the overall travel distance
     * @param depot the depot (may be null)
     * @param routes routes of all vehicles
     */
    public RoutingPlan(String distance, Location depot, List<RouteWithTrack> routes) {
        this.distance = Objects.requireNonNull(distance);
        this.depot = depot;
        this.routes = new ArrayList<>(Objects.requireNonNull(routes));
        if (depot == null && !routes.isEmpty()) {
            throw new IllegalArgumentException("Routes must be empty when depot is null.");
        }
    }

    /**
     * Create an empty routing plan.
     * @return empty routing plan
     */
    public static RoutingPlan empty() {
        return new RoutingPlan("0", null, Collections.emptyList());
    }

    /**
     * Overall travel distance.
     * @return travel distance
     */
    public String distance() {
        return distance;
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
