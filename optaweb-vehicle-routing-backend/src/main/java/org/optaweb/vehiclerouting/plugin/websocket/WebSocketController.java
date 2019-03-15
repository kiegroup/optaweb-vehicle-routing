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
import org.optaweb.vehiclerouting.domain.RoutingPlan;
import org.optaweb.vehiclerouting.service.demo.DemoService;
import org.optaweb.vehiclerouting.service.location.LocationService;
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
    private final DemoService demoService;

    @Autowired
    public WebSocketController(RouteListener routeListener,
                               RoutePublisherImpl routePublisher,
                               LocationService locationService,
                               DemoService demoService) {
        this.routeListener = routeListener;
        this.routePublisher = routePublisher;
        this.locationService = locationService;
        this.demoService = demoService;
    }

    /**
     * Subscribe for updates of the VRP route.
     * @return route message
     */
    @SubscribeMapping("/route")
    public PortableRoutingPlan subscribe() {
        logger.info("Subscribed");
        RoutingPlan routingPlan = routeListener.getBestRoutingPlan();
        return routePublisher.portable(routingPlan);
    }

    /**
     * Create new location.
     * @param request new location description
     */
    @MessageMapping("/location")
    public void addLocation(PortableLocation request) {
        locationService.createLocation(new LatLng(request.getLatitude(), request.getLongitude()));
    }

    /**
     * Delete location.
     * @param id ID of the location to be deleted
     */
    @MessageMapping({"/location/{id}/delete"})
    public void removeLocation(@DestinationVariable Long id) {
        locationService.removeLocation(id);
    }

    /**
     * Load a demo consisting of a number of cities.
     * @return number of demo locations
     */
    @MessageMapping("/demo")
    public int demo() {
        demoService.loadDemo();
        return demoService.getDemoSize();
    }

    @MessageMapping("/clear")
    public void clear() {
        locationService.clear();
    }
}
