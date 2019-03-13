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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.optaweb.vehiclerouting.domain.LatLng;
import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.domain.Route;
import org.optaweb.vehiclerouting.domain.RouteWithTrack;
import org.optaweb.vehiclerouting.service.route.RoutingPlan;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@RunWith(MockitoJUnitRunner.class)
public class RoutePublisherImplTest {

    @Mock
    private SimpMessagingTemplate webSocket;
    @InjectMocks
    private RoutePublisherImpl routePublisher;

    @Test
    public void publish() {
        routePublisher.publish(RoutingPlan.empty());
        Mockito.verify(webSocket).convertAndSend(anyString(), any(PortableRoutingPlan.class));
    }

    @Test
    public void portable_routing_plan_empty() {
        PortableRoutingPlan portablePlan = routePublisher.portable(RoutingPlan.empty());
        assertThat(portablePlan.getRoutes()).isEmpty();
        assertThat(portablePlan.getDistance()).isEqualTo("0");
    }

    @Test
    public void portable_routing_plan_with_two_routes() {
        final LatLng latLng1 = LatLng.valueOf(0.0, 0.1);
        final LatLng latLng2 = LatLng.valueOf(2.0, -0.2);
        final LatLng latLng3 = LatLng.valueOf(3.3, -3.3);
        final LatLng checkpoint12 = LatLng.valueOf(12, 12);
        final LatLng checkpoint21 = LatLng.valueOf(21, 21);
        final LatLng checkpoint13 = LatLng.valueOf(13, 13);
        final LatLng checkpoint31 = LatLng.valueOf(31, 31);
        List<LatLng> segment12 = Arrays.asList(latLng1, checkpoint12, latLng2);
        List<LatLng> segment21 = Arrays.asList(latLng2, checkpoint21, latLng1);
        List<LatLng> segment13 = Arrays.asList(latLng1, checkpoint13, latLng3);
        List<LatLng> segment31 = Arrays.asList(latLng3, checkpoint31, latLng1);

        final Location location1 = new Location(1, latLng1);
        final Location location2 = new Location(2, latLng2);
        final Location location3 = new Location(3, latLng3);
        final String distance = "xy";

        RouteWithTrack route1 = new RouteWithTrack(
                new Route(location1, location2),
                Arrays.asList(segment12, segment21)
        );
        RouteWithTrack route2 = new RouteWithTrack(
                new Route(location1, location3),
                Arrays.asList(segment13, segment31)
        );

        RoutingPlan routingPlan = new RoutingPlan(distance, Arrays.asList(route1, route2));

        PortableRoutingPlan portableRoutingPlan = routePublisher.portable(routingPlan);
        assertThat(portableRoutingPlan.getDistance()).isEqualTo(distance);
        assertThat(portableRoutingPlan.getRoutes()).hasSize(2);

        PortableRoute portableRoute1 = portableRoutingPlan.getRoutes().get(0);

        assertThat(portableRoute1.getVisits()).containsExactly(
                PortableLocation.fromLocation(location1),
                PortableLocation.fromLocation(location2)
        );
        assertThat(portableRoute1.getTrack()).hasSize(2);
        assertThat(portableRoute1.getTrack().get(0)).containsExactly(
                PortableLocation.fromLatLng(location1.getLatLng()),
                PortableLocation.fromLatLng(checkpoint12),
                PortableLocation.fromLatLng(location2.getLatLng())
        );
        assertThat(portableRoute1.getTrack().get(1)).containsExactly(
                PortableLocation.fromLatLng(location2.getLatLng()),
                PortableLocation.fromLatLng(checkpoint21),
                PortableLocation.fromLatLng(location1.getLatLng())
        );

        PortableRoute portableRoute2 = portableRoutingPlan.getRoutes().get(1);

        assertThat(portableRoute2.getVisits()).containsExactly(
                PortableLocation.fromLocation(location1),
                PortableLocation.fromLocation(location3)
        );
        assertThat(portableRoute2.getTrack()).hasSize(2);
        assertThat(portableRoute2.getTrack().get(0)).containsExactly(
                PortableLocation.fromLatLng(location1.getLatLng()),
                PortableLocation.fromLatLng(checkpoint13),
                PortableLocation.fromLatLng(location3.getLatLng())
        );
        assertThat(portableRoute2.getTrack().get(1)).containsExactly(
                PortableLocation.fromLatLng(location3.getLatLng()),
                PortableLocation.fromLatLng(checkpoint31),
                PortableLocation.fromLatLng(location1.getLatLng())
        );
    }
}
