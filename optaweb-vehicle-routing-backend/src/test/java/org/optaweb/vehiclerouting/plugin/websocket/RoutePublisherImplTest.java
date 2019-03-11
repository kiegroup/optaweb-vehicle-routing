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

import org.junit.Before;
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

    @Before
    public void setUp() {
    }

    @Test
    public void publish() {
        routePublisher.publish(RoutingPlan.empty());
        Mockito.verify(webSocket).convertAndSend(anyString(), any(PortableRoutingPlan.class));
    }

    @Test
    public void portableRoute_empty() {
        PortableRoutingPlan portablePlan = routePublisher.portable(RoutingPlan.empty());
        assertThat(portablePlan.getRoutes()).isEmpty();
        assertThat(portablePlan.getDistance()).isEqualTo("0");
    }

    @Test
    public void portableRoute_nonempty() {
        final LatLng latLng1 = LatLng.valueOf(0.0, 0.1);
        final LatLng latLng2 = LatLng.valueOf(2.0, -0.2);
        final LatLng checkpoint12 = LatLng.valueOf(12, 12);
        final LatLng checkpoint21 = LatLng.valueOf(21, 21);
        List<LatLng> segment1 = Arrays.asList(latLng1, checkpoint12, latLng2);
        List<LatLng> segment2 = Arrays.asList(latLng2, checkpoint21, latLng1);

        final Location location1 = new Location(1, latLng1);
        final Location location2 = new Location(2, latLng2);
        final String distance = "xy";

        RouteWithTrack route = new RouteWithTrack(
                new Route(location1, location2),
                Arrays.asList(segment1, segment2)
        );

        RoutingPlan routingPlan = new RoutingPlan(distance, Collections.singletonList(route));

        PortableRoutingPlan portableRoutingPlan = routePublisher.portable(routingPlan);
        assertThat(portableRoutingPlan.getDistance()).isEqualTo(distance);
        assertThat(portableRoutingPlan.getRoutes()).hasSize(1);

        PortableRoute portableRoute = portableRoutingPlan.getRoutes().iterator().next();

        assertThat(portableRoute.getVisits()).containsExactly(
                PortableLocation.fromLocation(location1),
                PortableLocation.fromLocation(location2)
        );
        assertThat(portableRoute.getTrack()).hasSize(2);
        assertThat(portableRoute.getTrack().get(0)).containsExactly(
                PortableLocation.fromLatLng(location1.getLatLng()),
                PortableLocation.fromLatLng(checkpoint12),
                PortableLocation.fromLatLng(location2.getLatLng())
        );
        assertThat(portableRoute.getTrack().get(1)).containsExactly(
                PortableLocation.fromLatLng(location2.getLatLng()),
                PortableLocation.fromLatLng(checkpoint21),
                PortableLocation.fromLatLng(location1.getLatLng())
        );
    }
}
