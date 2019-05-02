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
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.optaweb.vehiclerouting.domain.LatLng;
import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.domain.RouteWithTrack;
import org.optaweb.vehiclerouting.domain.RoutingPlan;
import org.optaweb.vehiclerouting.domain.RoutingProblem;
import org.optaweb.vehiclerouting.service.demo.DemoService;
import org.optaweb.vehiclerouting.service.location.LocationService;
import org.optaweb.vehiclerouting.service.region.BoundingBox;
import org.optaweb.vehiclerouting.service.region.RegionService;
import org.optaweb.vehiclerouting.service.route.RouteListener;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WebSocketControllerTest {

    @Mock
    private RouteListener routeListener;
    @Mock
    private RegionService regionService;
    @Mock
    private LocationService locationService;
    @Mock
    private DemoService demoService;
    @InjectMocks
    private WebSocketController webSocketController;

    @Test
    public void subscribeToRouteTopic() {
        // arrange
        String distance = "some distance";
        Location depot = new Location(1, LatLng.valueOf(3, 5));
        List<RouteWithTrack> routes = Collections.singletonList(mock(RouteWithTrack.class));
        RoutingPlan plan = new RoutingPlan(distance, depot, routes);
        when(routeListener.getBestRoutingPlan()).thenReturn(plan);

        // act
        PortableRoutingPlan portableRoutingPlan = webSocketController.subscribeToRouteTopic();

        // assert
        assertThat(portableRoutingPlan.getDistance()).isEqualTo(distance);
        assertThat(portableRoutingPlan.getDepot()).isEqualTo(PortableLocation.fromLocation(depot));
        assertThat(portableRoutingPlan.getRoutes()).hasSize(1);
    }

    @Test
    public void subscribeToServerInfo() {
        // arrange
        List<String> countryCodes = Arrays.asList("XY", "WZ");
        when(regionService.countryCodes()).thenReturn(countryCodes);

        LatLng southWest = LatLng.valueOf(-1.0, -2.0);
        LatLng northEast = LatLng.valueOf(1.0, 2.0);
        BoundingBox boundingBox = new BoundingBox(southWest, northEast);
        when(regionService.boundingBox()).thenReturn(boundingBox);

        Location depot = new Location(1, LatLng.valueOf(1.0, 7), "Depot");
        List<Location> visits = Arrays.asList(new Location(2, LatLng.valueOf(2.0, 9), "Visit"));
        String demoName = "Testing problem";
        RoutingProblem routingProblem = new RoutingProblem(demoName, depot, visits);
        when(demoService.demos()).thenReturn(Arrays.asList(routingProblem));

        // act
        ServerInfo serverInfo = webSocketController.subscribeToServerInfoTopic();

        // assert
        assertThat(serverInfo.getCountryCodes()).isEqualTo(countryCodes);
        assertThat(serverInfo.getBoundingBox()).containsExactly(
                PortableLocation.fromLatLng(southWest),
                PortableLocation.fromLatLng(northEast)
        );
        List<PortableRoutingProblem> demos = serverInfo.getDemos();
        assertThat(demos).hasSize(1);
        PortableRoutingProblem demo = demos.get(0);
        assertThat(demo.getName()).isEqualTo(demoName);
        assertThat(demo.getVisits()).isEqualTo(visits.size());
    }

    @Test
    public void addLocation() {
        LatLng latLng = LatLng.valueOf(0.0, 1.0);
        String description = "new location";
        PortableLocation request = new PortableLocation(321, latLng.getLatitude(), latLng.getLongitude(), description);
        webSocketController.addLocation(request);
        verify(locationService).createLocation(latLng, description);
    }

    @Test
    public void removeLocation() {
        webSocketController.removeLocation(9L);
        verify(locationService).removeLocation(9);
    }

    @Test
    public void demo() {
        String problemName = "xy";
        webSocketController.demo(problemName);
        verify(demoService).loadDemo(problemName);
    }

    @Test
    public void clear() {
        webSocketController.clear();
        verify(locationService).clear();
    }
}
