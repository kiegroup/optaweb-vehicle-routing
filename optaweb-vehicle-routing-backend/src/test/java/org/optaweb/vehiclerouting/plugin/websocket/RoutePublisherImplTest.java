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
import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.domain.Route;
import org.optaweb.vehiclerouting.domain.RouteWithTrack;
import org.optaweb.vehiclerouting.domain.RoutingPlan;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RoutePublisherImplTest {

    @Mock
    private SimpMessagingTemplate webSocket;
    @InjectMocks
    private RoutePublisherImpl routePublisher;

    @Test
    public void publish() {
        routePublisher.publish(RoutingPlan.empty());
        verify(webSocket).convertAndSend(anyString(), any(PortableRoutingPlan.class));
    }

    @Test
    public void portable_routing_plan_empty() {
        PortableRoutingPlan portablePlan = RoutePublisherImpl.portable(RoutingPlan.empty());
        assertThat(portablePlan.getDistance()).isEmpty();
        assertThat(portablePlan.getDepot()).isNull();
        assertThat(portablePlan.getRoutes()).isEmpty();
    }

    @Test
    public void portable_routing_plan_with_two_routes() {
        final Coordinates coordinates1 = Coordinates.valueOf(0.0, 0.1);
        final Coordinates coordinates2 = Coordinates.valueOf(2.0, -0.2);
        final Coordinates coordinates3 = Coordinates.valueOf(3.3, -3.3);
        final Coordinates checkpoint12 = Coordinates.valueOf(12, 12);
        final Coordinates checkpoint21 = Coordinates.valueOf(21, 21);
        final Coordinates checkpoint13 = Coordinates.valueOf(13, 13);
        final Coordinates checkpoint31 = Coordinates.valueOf(31, 31);
        List<Coordinates> segment12 = Arrays.asList(coordinates1, checkpoint12, coordinates2);
        List<Coordinates> segment21 = Arrays.asList(coordinates2, checkpoint21, coordinates1);
        List<Coordinates> segment13 = Arrays.asList(coordinates1, checkpoint13, coordinates3);
        List<Coordinates> segment31 = Arrays.asList(coordinates3, checkpoint31, coordinates1);

        final Location location1 = new Location(1, coordinates1);
        final Location location2 = new Location(2, coordinates2);
        final Location location3 = new Location(3, coordinates3);
        final String distance = "xy";

        RouteWithTrack route1 = new RouteWithTrack(
                new Route(location1, Collections.singletonList(location2)),
                Arrays.asList(segment12, segment21)
        );
        RouteWithTrack route2 = new RouteWithTrack(
                new Route(location1, Collections.singletonList(location3)),
                Arrays.asList(segment13, segment31)
        );

        RoutingPlan routingPlan = new RoutingPlan(distance, location1, Arrays.asList(route1, route2));

        PortableRoutingPlan portableRoutingPlan = RoutePublisherImpl.portable(routingPlan);
        assertThat(portableRoutingPlan.getDistance()).isEqualTo(distance);
        assertThat(portableRoutingPlan.getDepot()).isEqualTo(PortableLocation.fromLocation(location1));
        assertThat(portableRoutingPlan.getRoutes()).hasSize(2);

        PortableRoute portableRoute1 = portableRoutingPlan.getRoutes().get(0);

        assertThat(portableRoute1.getDepot()).isEqualTo(PortableLocation.fromLocation(location1));
        assertThat(portableRoute1.getVisits()).containsExactly(
                PortableLocation.fromLocation(location2)
        );
        assertThat(portableRoute1.getTrack()).hasSize(2);
        assertThat(portableRoute1.getTrack().get(0)).containsExactly(
                PortableLatLng.fromLatLng(location1.coordinates()),
                PortableLatLng.fromLatLng(checkpoint12),
                PortableLatLng.fromLatLng(location2.coordinates())
        );
        assertThat(portableRoute1.getTrack().get(1)).containsExactly(
                PortableLatLng.fromLatLng(location2.coordinates()),
                PortableLatLng.fromLatLng(checkpoint21),
                PortableLatLng.fromLatLng(location1.coordinates())
        );

        PortableRoute portableRoute2 = portableRoutingPlan.getRoutes().get(1);

        assertThat(portableRoute2.getDepot()).isEqualTo(PortableLocation.fromLocation(location1));
        assertThat(portableRoute2.getVisits()).containsExactly(
                PortableLocation.fromLocation(location3)
        );
        assertThat(portableRoute2.getTrack()).hasSize(2);
        assertThat(portableRoute2.getTrack().get(0)).containsExactly(
                PortableLatLng.fromLatLng(location1.coordinates()),
                PortableLatLng.fromLatLng(checkpoint13),
                PortableLatLng.fromLatLng(location3.coordinates())
        );
        assertThat(portableRoute2.getTrack().get(1)).containsExactly(
                PortableLatLng.fromLatLng(location3.coordinates()),
                PortableLatLng.fromLatLng(checkpoint31),
                PortableLatLng.fromLatLng(location1.coordinates())
        );
    }
}
