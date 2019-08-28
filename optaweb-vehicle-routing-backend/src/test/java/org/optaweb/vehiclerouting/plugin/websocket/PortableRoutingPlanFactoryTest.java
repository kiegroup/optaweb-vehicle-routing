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

package org.optaweb.vehiclerouting.plugin.websocket;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.domain.Route;
import org.optaweb.vehiclerouting.domain.RouteWithTrack;
import org.optaweb.vehiclerouting.domain.RoutingPlan;
import org.optaweb.vehiclerouting.domain.Vehicle;
import org.optaweb.vehiclerouting.domain.VehicleFactory;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

class PortableRoutingPlanFactoryTest {

    @Test
    void portable_routing_plan_empty() {
        PortableRoutingPlan portablePlan = PortableRoutingPlanFactory.fromRoutingPlan(RoutingPlan.empty());
        assertThat(portablePlan.getDistance()).isEmpty();
        assertThat(portablePlan.getVehicles()).isEmpty();
        assertThat(portablePlan.getDepot()).isNull();
        assertThat(portablePlan.getRoutes()).isEmpty();
    }

    @Test
    void portable_routing_plan_with_two_routes() {
        // arrange
        final Coordinates coordinates1 = Coordinates.valueOf(0.0, 0.1);
        final Coordinates coordinates2 = Coordinates.valueOf(2.0, -0.2);
        final Coordinates coordinates3 = Coordinates.valueOf(3.3, -3.3);
        final Coordinates checkpoint12 = Coordinates.valueOf(12, 12);
        final Coordinates checkpoint21 = Coordinates.valueOf(21, 21);
        final Coordinates checkpoint13 = Coordinates.valueOf(13, 13);
        final Coordinates checkpoint31 = Coordinates.valueOf(31, 31);
        List<Coordinates> segment12 = asList(coordinates1, checkpoint12, coordinates2);
        List<Coordinates> segment21 = asList(coordinates2, checkpoint21, coordinates1);
        List<Coordinates> segment13 = asList(coordinates1, checkpoint13, coordinates3);
        List<Coordinates> segment31 = asList(coordinates3, checkpoint31, coordinates1);

        final Location location1 = new Location(1, coordinates1);
        final Location location2 = new Location(2, coordinates2);
        final Location location3 = new Location(3, coordinates3);
        final String distance = "xy";

        final Vehicle vehicle1 = VehicleFactory.createVehicle(1, "Vehicle 1", 100);
        final Vehicle vehicle2 = VehicleFactory.createVehicle(2, "Vehicle 2", 200);

        RouteWithTrack route1 = new RouteWithTrack(
                new Route(vehicle1, location1, singletonList(location2)),
                asList(segment12, segment21)
        );
        RouteWithTrack route2 = new RouteWithTrack(
                new Route(vehicle2, location1, singletonList(location3)),
                asList(segment13, segment31)
        );

        RoutingPlan routingPlan = new RoutingPlan(
                distance,
                asList(vehicle1, vehicle2),
                location1,
                asList(location2, location3),
                asList(route1, route2)
        );

        // act
        PortableRoutingPlan portableRoutingPlan = PortableRoutingPlanFactory.fromRoutingPlan(routingPlan);

        // assert
        // -- plan.distance
        assertThat(portableRoutingPlan.getDistance()).isEqualTo(distance);
        // -- plan.depot
        assertThat(portableRoutingPlan.getDepot()).isEqualTo(PortableLocation.fromLocation(location1));
        // -- plan.visits
        assertThat(portableRoutingPlan.getVisits()).containsExactlyInAnyOrder(
                PortableLocation.fromLocation(location2),
                PortableLocation.fromLocation(location3)
        );
        // -- plan.routes
        assertThat(portableRoutingPlan.getRoutes()).hasSize(2);
        // -- plan.vehicles
        assertThat(portableRoutingPlan.getVehicles()).containsExactlyInAnyOrder(
                PortableVehicle.fromVehicle(vehicle1),
                PortableVehicle.fromVehicle(vehicle2)
        );

        // -- plan.routes[1]
        PortableRoute portableRoute1 = portableRoutingPlan.getRoutes().get(0);

        assertThat(portableRoute1.getVehicle()).isEqualTo(PortableVehicle.fromVehicle(vehicle1));
        assertThat(portableRoute1.getDepot()).isEqualTo(PortableLocation.fromLocation(location1));
        assertThat(portableRoute1.getVisits()).containsExactly(
                PortableLocation.fromLocation(location2)
        );
        assertThat(portableRoute1.getTrack()).hasSize(2);
        assertThat(portableRoute1.getTrack().get(0)).containsExactly(
                PortableCoordinates.fromCoordinates(location1.coordinates()),
                PortableCoordinates.fromCoordinates(checkpoint12),
                PortableCoordinates.fromCoordinates(location2.coordinates())
        );
        assertThat(portableRoute1.getTrack().get(1)).containsExactly(
                PortableCoordinates.fromCoordinates(location2.coordinates()),
                PortableCoordinates.fromCoordinates(checkpoint21),
                PortableCoordinates.fromCoordinates(location1.coordinates())
        );

        // -- plan.routes[2]
        PortableRoute portableRoute2 = portableRoutingPlan.getRoutes().get(1);

        assertThat(portableRoute2.getVehicle()).isEqualTo(PortableVehicle.fromVehicle(vehicle2));
        assertThat(portableRoute2.getDepot()).isEqualTo(PortableLocation.fromLocation(location1));
        assertThat(portableRoute2.getVisits()).containsExactly(
                PortableLocation.fromLocation(location3)
        );
        assertThat(portableRoute2.getTrack()).hasSize(2);
        assertThat(portableRoute2.getTrack().get(0)).containsExactly(
                PortableCoordinates.fromCoordinates(location1.coordinates()),
                PortableCoordinates.fromCoordinates(checkpoint13),
                PortableCoordinates.fromCoordinates(location3.coordinates())
        );
        assertThat(portableRoute2.getTrack().get(1)).containsExactly(
                PortableCoordinates.fromCoordinates(location3.coordinates()),
                PortableCoordinates.fromCoordinates(checkpoint31),
                PortableCoordinates.fromCoordinates(location1.coordinates())
        );
    }
}
