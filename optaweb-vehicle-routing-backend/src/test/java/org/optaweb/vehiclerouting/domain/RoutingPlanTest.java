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
import java.util.List;

import org.junit.jupiter.api.Test;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class RoutingPlanTest {

    private final Vehicle vehicle = new Vehicle(1, "V");
    private final List<Vehicle> vehicles = singletonList(vehicle);
    private final Location depot = new Location(1, Coordinates.valueOf(5, 5));
    private final RouteWithTrack emptyRoute = new RouteWithTrack(new Route(vehicle, depot, emptyList()), emptyList());

    @Test
    void constructor_args_not_null() {
        assertThatNullPointerException().isThrownBy(() -> new RoutingPlan(null, vehicles, depot, emptyList()));
        assertThatNullPointerException().isThrownBy(() -> new RoutingPlan("", null, depot, emptyList()));
        assertThatNullPointerException().isThrownBy(() -> new RoutingPlan("", vehicles, depot, null));
        // depot can be null
        // TODO create a factory that will prevent passing a null depot accidentally
    }

    @Test
    void no_routes_without_a_depot() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new RoutingPlan("", vehicles, null, singletonList(emptyRoute)));
    }

    @Test
    void there_must_be_one_route_per_vehicle_when_there_is_a_depot() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new RoutingPlan("", vehicles, depot, emptyList()))
                .withMessageContaining("Vehicles (1): [")
                .withMessageContaining("Routes' vehicleIds (0): []");
    }

    @Test
    void routes_vehicle_references_must_be_consistent_with_vehicles_in_routing_plan() {
        List<Vehicle> newVehicles = singletonList(new Vehicle(vehicle.id() + 1, ""));
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new RoutingPlan("", newVehicles, depot, singletonList(emptyRoute)))
                .withMessageContaining("Vehicles (1): [")
                .withMessageContaining("Routes' vehicleIds (1): [" + vehicle.id() + "]");
    }

    @Test
    void cannot_modify_routes_externally() {
        ArrayList<RouteWithTrack> routes = new ArrayList<>();
        routes.add(emptyRoute);
        RoutingPlan routingPlan = new RoutingPlan("", vehicles, depot, routes);

        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> routingPlan.routes().clear());
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> routingPlan.vehicles().clear());
    }
}
