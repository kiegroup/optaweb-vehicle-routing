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

package org.optaweb.vehiclerouting.plugin.websocket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.optaweb.vehiclerouting.domain.LatLng;
import org.optaweb.vehiclerouting.service.route.RoutePublisher;
import org.optaweb.vehiclerouting.service.route.RoutingPlan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * Broadcasts updated route to interested clients over WebSocket.
 */
@Component
public class RoutePublisherImpl implements RoutePublisher {

    private final SimpMessagingTemplate webSocket;

    @Autowired
    public RoutePublisherImpl(SimpMessagingTemplate webSocket) {
        this.webSocket = webSocket;
    }

    @Override
    public void publish(RoutingPlan routingPlan) {
        webSocket.convertAndSend("/topic/route", portable(routingPlan));
    }

    PortableRoute portable(RoutingPlan routingPlan) {
        if (routingPlan.routes().isEmpty()) {
            return new PortableRoute(routingPlan.distance(), Collections.emptyList(), Collections.emptyList());
        }
        // FIXME don't ignore other routes (as soon as we add more vehicles)
        List<PortableLocation> portableRoute = routingPlan.routes().get(0).visits().stream()
                .map(PortableLocation::fromLocation)
                .collect(Collectors.toList());
        List<List<PortableLocation>> portableSegments = new ArrayList<>();
        for (List<LatLng> segment : routingPlan.routes().get(0).track()) {
            List<PortableLocation> portableSegment = segment.stream()
                    .map(PortableLocation::fromLatLng)
                    .collect(Collectors.toList());
            portableSegments.add(portableSegment);
        }
        return new PortableRoute(routingPlan.distance(), portableRoute, portableSegments);
    }
}
