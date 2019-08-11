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

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.domain.Route;
import org.optaweb.vehiclerouting.domain.RouteWithTrack;
import org.optaweb.vehiclerouting.domain.RoutingPlan;
import org.optaweb.vehiclerouting.domain.RoutingProblem;
import org.optaweb.vehiclerouting.domain.Vehicle;
import org.optaweb.vehiclerouting.domain.VehicleFactory;
import org.optaweb.vehiclerouting.service.demo.DemoService;
import org.optaweb.vehiclerouting.service.location.LocationService;
import org.optaweb.vehiclerouting.service.region.BoundingBox;
import org.optaweb.vehiclerouting.service.region.RegionService;
import org.optaweb.vehiclerouting.service.route.RouteListener;
import org.optaweb.vehiclerouting.service.vehicle.VehicleService;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WebSocketControllerTest {

    @Mock
    private RouteListener routeListener;
    @Mock
    private RegionService regionService;
    @Mock
    private VehicleService vehicleService;
    @Mock
    private LocationService locationService;
    @Mock
    private DemoService demoService;
    @InjectMocks
    private WebSocketController webSocketController;

    @Test
    void subscribeToRouteTopic() {
        // arrange
        String distance = "some distance";
        Location depot = new Location(1, Coordinates.valueOf(3, 5));
        Vehicle vehicle = VehicleFactory.createVehicle(1, "vehicle", 77);
        Location visit = new Location(2, Coordinates.valueOf(321, 123));
        Route route = new Route(vehicle, depot, singletonList(visit));
        Coordinates pointOnTrack = Coordinates.valueOf(0, 0);
        RouteWithTrack routeWithTrack = new RouteWithTrack(route, singletonList(singletonList(pointOnTrack)));
        RoutingPlan plan = new RoutingPlan(
                distance,
                singletonList(vehicle),
                depot,
                singletonList(visit),
                singletonList(routeWithTrack)
        );
        when(routeListener.getBestRoutingPlan()).thenReturn(plan);

        // act
        PortableRoutingPlan portableRoutingPlan = webSocketController.subscribeToRouteTopic();

        // assert
        assertThat(portableRoutingPlan.getDistance()).isEqualTo(distance);
        assertThat(portableRoutingPlan.getVisits()).containsExactly(PortableLocation.fromLocation(visit));
        assertThat(portableRoutingPlan.getVehicles()).containsExactly(PortableVehicle.fromVehicle(vehicle));
        assertThat(portableRoutingPlan.getDepot()).isEqualTo(PortableLocation.fromLocation(depot));
        assertThat(portableRoutingPlan.getRoutes()).hasSize(1);
    }

    @Test
    void subscribeToServerInfo() {
        // arrange
        List<String> countryCodes = Arrays.asList("XY", "WZ");
        when(regionService.countryCodes()).thenReturn(countryCodes);

        Coordinates southWest = Coordinates.valueOf(-1.0, -2.0);
        Coordinates northEast = Coordinates.valueOf(1.0, 2.0);
        BoundingBox boundingBox = new BoundingBox(southWest, northEast);
        when(regionService.boundingBox()).thenReturn(boundingBox);

        Location depot = new Location(1, Coordinates.valueOf(1.0, 7), "Depot");
        List<Location> visits = Arrays.asList(new Location(2, Coordinates.valueOf(2.0, 9), "Visit"));
        String demoName = "Testing problem";
        RoutingProblem routingProblem = new RoutingProblem(demoName, depot, visits);
        when(demoService.demos()).thenReturn(Arrays.asList(routingProblem));

        // act
        ServerInfo serverInfo = webSocketController.subscribeToServerInfoTopic();

        // assert
        assertThat(serverInfo.getCountryCodes()).isEqualTo(countryCodes);
        assertThat(serverInfo.getBoundingBox()).containsExactly(
                PortableCoordinates.fromCoordinates(southWest),
                PortableCoordinates.fromCoordinates(northEast)
        );
        List<RoutingProblemInfo> demos = serverInfo.getDemos();
        assertThat(demos).hasSize(1);
        RoutingProblemInfo demo = demos.get(0);
        assertThat(demo.getName()).isEqualTo(demoName);
        assertThat(demo.getVisits()).isEqualTo(visits.size());
    }

    @Test
    void addLocation() {
        Coordinates coords = Coordinates.valueOf(0.0, 1.0);
        String description = "new location";
        PortableLocation request = new PortableLocation(321, coords.latitude(), coords.longitude(), description);
        webSocketController.addLocation(request);
        verify(locationService).createLocation(coords, description);
    }

    @Test
    void removeLocation() {
        webSocketController.removeLocation(9L);
        verify(locationService).removeLocation(9);
    }

    @Test
    void addVehicle() {
        webSocketController.addVehicle();
        verify(vehicleService).addVehicle();
    }

    @Test
    void removeVehicle() {
        webSocketController.removeVehicle(11L);
        verify(vehicleService).removeVehicle(11);
    }

    @Test
    void removeAnyVehicle() {
        webSocketController.removeAnyVehicle();
        verify(vehicleService).removeAnyVehicle();
    }

    @Test
    void changeCapacity() {
        long vehicleId = 2000;
        int capacity = 50;
        webSocketController.changeCapacity(vehicleId, capacity);
        verify(vehicleService).changeCapacity(vehicleId, capacity);
    }

    @Test
    void demo() {
        String problemName = "xy";
        webSocketController.demo(problemName);
        verify(demoService).loadDemo(problemName);
    }

    @Test
    void clear() {
        webSocketController.clear();
        verify(locationService).removeAll();
        verify(vehicleService).removeAll();
    }
}
