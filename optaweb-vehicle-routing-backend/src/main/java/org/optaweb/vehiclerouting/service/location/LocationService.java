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

package org.optaweb.vehiclerouting.service.location;

import static java.util.Comparator.comparingLong;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.service.error.ErrorEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * Performs location-related use cases.
 */
@Service
public class LocationService {

    private static final Logger logger = LoggerFactory.getLogger(LocationService.class);

    private final LocationRepository repository;
    private final RouteOptimizer optimizer; // TODO move to RoutingPlanService (SRP)
    private final DistanceMatrix distanceMatrix;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    LocationService(
            LocationRepository repository,
            RouteOptimizer optimizer,
            DistanceMatrix distanceMatrix,
            ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.optimizer = optimizer;
        this.distanceMatrix = distanceMatrix;
        this.eventPublisher = eventPublisher;
    }

    public synchronized boolean createLocation(Coordinates coordinates, String description) {
        Objects.requireNonNull(coordinates);
        Objects.requireNonNull(description);
        // TODO if (router.isLocationAvailable(coordinates))
        return submitToPlanner(repository.createLocation(coordinates, description));
    }

    public synchronized boolean addLocation(Location location) {
        return submitToPlanner(Objects.requireNonNull(location));
    }

    private boolean submitToPlanner(Location location) {
        try {
            DistanceMatrixRow distanceMatrixRow = distanceMatrix.addLocation(location);
            optimizer.addLocation(location, distanceMatrixRow);
        } catch (Exception e) {
            logger.error(
                    "Failed to calculate distances for location {}, it will be discarded",
                    location.fullDescription(), e);
            eventPublisher.publishEvent(new ErrorEvent(
                    this,
                    "Failed to calculate distances for location " + location.fullDescription()
                            + ", it will be discarded.\n" + e.toString()));
            repository.removeLocation(location.id());
            return false; // do not proceed to optimizer
        }
        return true;
    }

    public synchronized void removeLocation(long id) {
        Optional<Location> optionalLocation = repository.find(id);
        if (!optionalLocation.isPresent()) {
            eventPublisher.publishEvent(
                    new ErrorEvent(this, "Location [" + id + "] cannot be removed because it doesn't exist."));
            return;
        }
        Location removedLocation = optionalLocation.get();
        List<Location> locations = repository.locations();
        if (locations.size() > 1) {
            Location depot = locations.stream()
                    .min(comparingLong(Location::id))
                    .orElseThrow(() -> new IllegalStateException(
                            "Impossible. Locations have size (" + locations.size() + ") but the stream is empty."));
            if (removedLocation.equals(depot)) {
                eventPublisher.publishEvent(
                        new ErrorEvent(this, "You can only remove depot if there are no visits."));
                return;
            }
        }

        optimizer.removeLocation(removedLocation);
        repository.removeLocation(id);
        distanceMatrix.removeLocation(removedLocation);
    }

    public synchronized void removeAll() {
        optimizer.removeAllLocations();
        repository.removeAll();
        distanceMatrix.clear();
    }
}
