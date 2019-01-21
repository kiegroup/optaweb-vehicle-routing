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

import org.optaweb.vehiclerouting.domain.LatLng;
import org.optaweb.vehiclerouting.service.location.LocationService;
import org.optaweb.vehiclerouting.service.route.Route;
import org.optaweb.vehiclerouting.service.route.RouteListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

/**
 * Handles WebSocket subscriptions and STOMP messages.
 * @see WebSocketConfig
 */
@Controller
public class WebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketController.class);

    private final RouteListener routeListener;
    private final RoutePublisherImpl routePublisher;
    private final LocationService locationService;

    @Autowired
    public WebSocketController(RouteListener routeListener,
                               RoutePublisherImpl routePublisher,
                               LocationService locationService) {
        this.routeListener = routeListener;
        this.routePublisher = routePublisher;
        this.locationService = locationService;
    }

    /**
     * Subscribe for updates of the TSP route.
     * @return route message
     */
    @SubscribeMapping("/route")
    public PortableRoute subscribe() {
        logger.info("Subscribed");
        Route route = routeListener.getBestRoute();
        return routePublisher.portableRoute(route);
    }

    /**
     * Create new location.
     * @param request new location description
     */
    @MessageMapping("/place") // TODO rename to location
    public void addLocation(PortableLocation request) {
        locationService.addLocation(new LatLng(request.getLatitude(), request.getLongitude()));
    }

    /**
     * Delete location.
     * @param id ID of the location to be deleted
     */
    @MessageMapping({"/place/{id}/delete"}) // TODO rename to location
    public void removeLocation(@DestinationVariable Long id) {
        locationService.removeLocation(id);
    }

    /**
     * Load a demo consisting of a number of cities.
     */
    @MessageMapping("/demo")
    public int demo() {
        locationService.loadDemo();
        return locationService.getDemoSize();
    }

    @MessageMapping("/clear")
    public void clear() {
        locationService.clear();
    }
}
