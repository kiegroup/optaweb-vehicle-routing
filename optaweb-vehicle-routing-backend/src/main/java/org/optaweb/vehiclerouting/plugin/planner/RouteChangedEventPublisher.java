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
import java.util.stream.Collectors;

import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.Depot;
import org.optaplanner.examples.vehiclerouting.domain.Vehicle;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaweb.vehiclerouting.service.route.RouteChangedEvent;
import org.optaweb.vehiclerouting.service.route.ShallowRoute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Converts planning solution to a {@link RouteChangedEvent} and publishes it so that it can be processed by other
 * components that listen for this type of event.
 */
@Component
class RouteChangedEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(RouteChangedEventPublisher.class);

    private final ApplicationEventPublisher publisher;

    RouteChangedEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    /**
     * Publish solution as a {@link RouteChangedEvent}.
     * @param solution solution
     */
    void publishRoute(VehicleRoutingSolution solution) {
        RouteChangedEvent event = solutionToEvent(solution, this);
        logger.info(
                "New solution with {} depots, {} vehicles, {} customers, distance: {}",
                solution.getDepotList().size(),
                solution.getVehicleList().size(),
                solution.getCustomerList().size(),
                event.distance()
        );
        logger.debug("Routes: {}", event.routes());
        publisher.publishEvent(event);
    }

    /**
     * Convert a planning domain solution to an event that can be published.
     * @param solution solution
     * @param source source of the event
     * @return new event describing the solution
     */
    static RouteChangedEvent solutionToEvent(VehicleRoutingSolution solution, Object source) {
        String distanceString = solution.getDistanceString(null).replaceFirst(" \\d+ms$", "");
        List<ShallowRoute> routes = routes(solution);
        return new RouteChangedEvent(
                source,
                distanceString,
                vehicleIds(solution),
                depotId(solution),
                routes
        );
    }

    /**
     * Extract routes from the solution. Includes empty routes of vehicles that stay in the depot.
     * @param solution solution
     * @return one route per vehicle
     */
    private static List<ShallowRoute> routes(VehicleRoutingSolution solution) {
        // TODO include unconnected customers in the result
        if (solution.getDepotList().isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<ShallowRoute> routes = new ArrayList<>();
        for (Vehicle vehicle : solution.getVehicleList()) {
            Depot depot = vehicle.getDepot();
            if (depot == null) {
                throw new IllegalArgumentException(
                        "Vehicle (id=" + vehicle.getId() + ") is not in the depot. That's not allowed"
                );
            }
            List<Long> visits = new ArrayList<>();
            for (
                    Customer customer = vehicle.getNextCustomer();
                    customer != null;
                    customer = customer.getNextCustomer()
            ) {
                if (!solution.getCustomerList().contains(customer)) {
                    throw new IllegalArgumentException("Customer (" + customer + ") doesn't exist");
                }
                visits.add(customer.getLocation().getId());
            }
            routes.add(new ShallowRoute(vehicle.getId(), depot.getId(), visits));
        }
        return routes;
    }

    /**
     * Get IDs of vehicles in the solution.
     * @param solution the solution
     * @return vehicle IDs
     */
    private static List<Long> vehicleIds(VehicleRoutingSolution solution) {
        return solution.getVehicleList().stream()
                .map(Vehicle::getId)
                .collect(Collectors.toList());
    }

    /**
     * Get solution's depot ID.
     * @param solution the solution in which to look for the depot
     * @return first depot ID from the solution or {@code null} if there are no depots
     */
    private static Long depotId(VehicleRoutingSolution solution) {
        return solution.getDepotList().isEmpty() ? null : solution.getDepotList().get(0).getId();
    }
}
