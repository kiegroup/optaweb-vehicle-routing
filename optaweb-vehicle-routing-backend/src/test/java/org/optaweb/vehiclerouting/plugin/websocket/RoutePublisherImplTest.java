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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.LocationNew;
import org.optaweb.vehiclerouting.domain.Route;
import org.optaweb.vehiclerouting.domain.RouteWithTrack;
import org.optaweb.vehiclerouting.domain.RoutingPlan;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RoutePublisherImplTest {

    @Mock
    private SimpMessagingTemplate webSocket;
    @InjectMocks
    private RoutePublisherImpl routePublisher;

    @Test
    void publish() {
        routePublisher.publish(RoutingPlan.empty());
        verify(webSocket).convertAndSend(anyString(), any(PortableRoutingPlan.class));
    }

    @Test
    void portable_routing_plan_empty() {
        PortableRoutingPlan portablePlan = RoutePublisherImpl.portable(RoutingPlan.empty());
        assertThat(portablePlan.getDistance()).isEmpty();
        assertThat(portablePlan.getDepot()).isNull();
        assertThat(portablePlan.getRoutes()).isEmpty();
    }

    @Test
    void portable_routing_plan_with_two_routes() {
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

        final LocationNew locationNew1 = new LocationNew(1, coordinates1);
        final LocationNew locationNew2 = new LocationNew(2, coordinates2);
        final LocationNew locationNew3 = new LocationNew(3, coordinates3);
        final String distance = "xy";

        RouteWithTrack route1 = new RouteWithTrack(
                new Route(locationNew1, Collections.singletonList(locationNew2)),
                Arrays.asList(segment12, segment21)
        );
        RouteWithTrack route2 = new RouteWithTrack(
                new Route(locationNew1, Collections.singletonList(locationNew3)),
                Arrays.asList(segment13, segment31)
        );

        RoutingPlan routingPlan = new RoutingPlan(distance, locationNew1, Arrays.asList(route1, route2));

        PortableRoutingPlan portableRoutingPlan = RoutePublisherImpl.portable(routingPlan);
        assertThat(portableRoutingPlan.getDistance()).isEqualTo(distance);
        assertThat(portableRoutingPlan.getDepot()).isEqualTo(PortableLocation.fromLocation(locationNew1));
        assertThat(portableRoutingPlan.getRoutes()).hasSize(2);

        PortableRoute portableRoute1 = portableRoutingPlan.getRoutes().get(0);

        assertThat(portableRoute1.getDepot()).isEqualTo(PortableLocation.fromLocation(locationNew1));
        assertThat(portableRoute1.getVisits()).containsExactly(
                PortableLocation.fromLocation(locationNew2)
        );
        assertThat(portableRoute1.getTrack()).hasSize(2);
        assertThat(portableRoute1.getTrack().get(0)).containsExactly(
                PortableCoordinates.fromCoordinates(locationNew1.coordinates()),
                PortableCoordinates.fromCoordinates(checkpoint12),
                PortableCoordinates.fromCoordinates(locationNew2.coordinates())
        );
        assertThat(portableRoute1.getTrack().get(1)).containsExactly(
                PortableCoordinates.fromCoordinates(locationNew2.coordinates()),
                PortableCoordinates.fromCoordinates(checkpoint21),
                PortableCoordinates.fromCoordinates(locationNew1.coordinates())
        );

        PortableRoute portableRoute2 = portableRoutingPlan.getRoutes().get(1);

        assertThat(portableRoute2.getDepot()).isEqualTo(PortableLocation.fromLocation(locationNew1));
        assertThat(portableRoute2.getVisits()).containsExactly(
                PortableLocation.fromLocation(locationNew3)
        );
        assertThat(portableRoute2.getTrack()).hasSize(2);
        assertThat(portableRoute2.getTrack().get(0)).containsExactly(
                PortableCoordinates.fromCoordinates(locationNew1.coordinates()),
                PortableCoordinates.fromCoordinates(checkpoint13),
                PortableCoordinates.fromCoordinates(locationNew3.coordinates())
        );
        assertThat(portableRoute2.getTrack().get(1)).containsExactly(
                PortableCoordinates.fromCoordinates(locationNew3.coordinates()),
                PortableCoordinates.fromCoordinates(checkpoint31),
                PortableCoordinates.fromCoordinates(locationNew1.coordinates())
        );
    }
}
