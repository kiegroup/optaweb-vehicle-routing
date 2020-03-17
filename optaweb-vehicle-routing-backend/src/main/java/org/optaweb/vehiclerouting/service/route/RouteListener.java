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

package org.optaweb.vehiclerouting.service.route;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.domain.Route;
import org.optaweb.vehiclerouting.domain.RouteWithTrack;
import org.optaweb.vehiclerouting.domain.RoutingPlan;
import org.optaweb.vehiclerouting.domain.Vehicle;
import org.optaweb.vehiclerouting.service.location.LocationRepository;
import org.optaweb.vehiclerouting.service.vehicle.VehicleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import static org.optaweb.vehiclerouting.Profiles.NOT_TEST;

/**
 * Handles route updates emitted by optimization plugin.
 */
@Service
@Profile(NOT_TEST)
public class RouteListener implements ApplicationListener<RouteChangedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(RouteListener.class);

    private final Router router;
    private final RoutePublisher publisher;
    private final VehicleRepository vehicleRepository;
    private final LocationRepository locationRepository;

    // TODO maybe remove state from the service and get best route from a repository
    private RoutingPlan bestRoutingPlan;

    @Autowired
    RouteListener(
            Router router,
            RoutePublisher publisher,
            VehicleRepository vehicleRepository,
            LocationRepository locationRepository
    ) {
        this.router = router;
        this.publisher = publisher;
        this.vehicleRepository = vehicleRepository;
        this.locationRepository = locationRepository;
        bestRoutingPlan = RoutingPlan.empty();
    }

    @Override
    public void onApplicationEvent(RouteChangedEvent event) {
        // TODO persist the best solution
        Location depot = event.depotId().flatMap(locationRepository::find).orElse(null);
        try {
            // TODO Introduce problem revision (every modification increases revision number, event will only
            //  be published if revision numbers match) to avoid looking for missing/extra vehicles/visits.
            //  This will also make it possible to get rid of the try-catch approach.
            Map<Long, Vehicle> vehicleMap = event.vehicleIds().stream()
                    .collect(Collectors.toMap(vehicleId -> vehicleId, this::findVehicleById));
            Map<Long, Location> visitMap = event.visitIds().stream()
                    .collect(Collectors.toMap(visitId -> visitId, this::findLocationById));

            List<RouteWithTrack> routes = event.routes().stream()
                    // list of deep locations
                    .map(shallowRoute -> new Route(
                            vehicleMap.get(shallowRoute.vehicleId),
                            findLocationById(shallowRoute.depotId),
                            shallowRoute.visitIds.stream()
                                    .map(visitMap::get)
                                    .collect(Collectors.toList())
                    ))
                    // add tracks
                    .map(route -> new RouteWithTrack(route, track(route.depot(), route.visits())))
                    .collect(Collectors.toList());
            bestRoutingPlan = new RoutingPlan(
                    event.distance(),
                    new ArrayList<>(vehicleMap.values()),
                    depot,
                    new ArrayList<>(visitMap.values()),
                    routes
            );
            publisher.publish(bestRoutingPlan);
        } catch (IllegalStateException e) {
            logger.warn("Discarding an outdated routing plan: {}", e.toString());
        }
    }

    private Vehicle findVehicleById(Long id) {
        return vehicleRepository.find(id).orElseThrow(() -> new IllegalStateException(
                "Vehicle {id=" + id + "} not found in the repository")
        );
    }

    private Location findLocationById(Long id) {
        return locationRepository.find(id).orElseThrow(() -> new IllegalStateException(
                "Location {id=" + id + "} not found in the repository")
        );
    }

    private List<List<Coordinates>> track(Location depot, List<Location> route) {
        if (route.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<Location> itinerary = new ArrayList<>();
        itinerary.add(depot);
        itinerary.addAll(route);
        itinerary.add(depot);
        List<List<Coordinates>> paths = new ArrayList<>();
        for (int i = 0; i < itinerary.size() - 1; i++) {
            Location fromLocation = itinerary.get(i);
            Location toLocation = itinerary.get(i + 1);
            List<Coordinates> path = router.getPath(fromLocation.coordinates(), toLocation.coordinates());
            paths.add(path);
        }
        return paths;
    }

    public RoutingPlan getBestRoutingPlan() {
        return bestRoutingPlan;
    }
}
