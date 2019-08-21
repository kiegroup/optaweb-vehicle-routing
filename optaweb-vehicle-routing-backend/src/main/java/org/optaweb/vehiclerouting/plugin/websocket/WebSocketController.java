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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.RoutingPlan;
import org.optaweb.vehiclerouting.service.demo.DemoService;
import org.optaweb.vehiclerouting.service.location.LocationService;
import org.optaweb.vehiclerouting.service.region.BoundingBox;
import org.optaweb.vehiclerouting.service.region.RegionService;
import org.optaweb.vehiclerouting.service.route.RouteListener;
import org.optaweb.vehiclerouting.service.vehicle.VehicleService;
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
class WebSocketController {

    private final RouteListener routeListener;
    private final RegionService regionService;
    private final LocationService locationService;
    private final VehicleService vehicleService;
    private final DemoService demoService;

    @Autowired
    WebSocketController(
            RouteListener routeListener,
            RegionService regionService,
            LocationService locationService,
            VehicleService vehicleService,
            DemoService demoService
    ) {
        this.routeListener = routeListener;
        this.regionService = regionService;
        this.locationService = locationService;
        this.vehicleService = vehicleService;
        this.demoService = demoService;
    }

    /**
     * Subscribe to server info topic.
     * @return server info
     */
    @SubscribeMapping("/serverInfo")
    ServerInfo subscribeToServerInfoTopic() {
        BoundingBox boundingBox = regionService.boundingBox();
        List<PortableCoordinates> portableBoundingBox = Arrays.asList(
                PortableCoordinates.fromCoordinates(boundingBox.getSouthWest()),
                PortableCoordinates.fromCoordinates(boundingBox.getNorthEast()));
        List<RoutingProblemInfo> demos = demoService.demos().stream()
                .map(routingProblem -> new RoutingProblemInfo(
                        routingProblem.name(),
                        routingProblem.visits().size()))
                .collect(Collectors.toList());
        return new ServerInfo(portableBoundingBox, regionService.countryCodes(), demos);
    }

    /**
     * Subscribe for updates of the VRP route.
     * @return route message
     */
    @SubscribeMapping("/route")
    PortableRoutingPlan subscribeToRouteTopic() {
        RoutingPlan routingPlan = routeListener.getBestRoutingPlan();
        return PortableRoutingPlanFactory.fromRoutingPlan(routingPlan);
    }

    /**
     * Create new location.
     * @param request new location description
     */
    @MessageMapping("/location")
    void addLocation(PortableLocation request) {
        locationService.createLocation(
                new Coordinates(request.getLatitude(), request.getLongitude()),
                request.getDescription()
        );
    }

    /**
     * Delete location.
     * @param id ID of the location to be deleted
     */
    @MessageMapping({"/location/{id}/delete"})
    void removeLocation(@DestinationVariable Long id) {
        locationService.removeLocation(id);
    }

    /**
     * Load a demo data set.
     * @param name data set name
     */
    @MessageMapping("/demo/{name}")
    void demo(@DestinationVariable String name) {
        demoService.loadDemo(name);
    }

    @MessageMapping("/clear")
    void clear() {
        // TODO do this in one step (=> new RoutingPlanService)
        locationService.removeAll();
        vehicleService.removeAll();
    }

    @MessageMapping({"vehicle"})
    void addVehicle() {
        vehicleService.addVehicle();
    }

    /**
     * Delete vehicle.
     * @param id ID of the vehicle to be deleted
     */
    @MessageMapping({"/vehicle/{id}/delete"})
    void removeVehicle(@DestinationVariable Long id) {
        vehicleService.removeVehicle(id);
    }

    @MessageMapping({"/vehicle/deleteAny"})
    void removeAnyVehicle() {
        vehicleService.removeAnyVehicle();
    }

    @MessageMapping({"/vehicle/{id}/capacity"})
    void changeCapacity(@DestinationVariable Long id, int capacity) {
        vehicleService.changeCapacity(id, capacity);
    }
}
