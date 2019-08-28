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

import java.util.Objects;

import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    LocationService(
            LocationRepository repository,
            RouteOptimizer optimizer,
            DistanceMatrix distanceMatrix
    ) {
        this.repository = repository;
        this.optimizer = optimizer;
        this.distanceMatrix = distanceMatrix;
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
            distanceMatrix.addLocation(location);
        } catch (Exception e) {
            // TODO relay the error event to the client
            logger.warn("Failed to calculate distances for {}, it will be discarded", location);
            logger.debug("Details:", e);
            repository.removeLocation(location.id());
            return false; // do not proceed to optimizer
        }
        optimizer.addLocation(location, distanceMatrix);
        return true;
    }

    public synchronized void removeLocation(long id) {
        // TODO missing validation (id might be out of date or completely invalid)
        // A) Take Location as an argument => it's valid but still might be out of date. Decide how to handle that
        // case.
        // B) removeLocation() returns Optional instead of throwing IllArgException. Still need to decide how to handle
        //    the case when location with given ID doesn't exist.
        Location location = repository.removeLocation(id);
        optimizer.removeLocation(location);
    }

    public synchronized void removeAll() {
        optimizer.removeAllLocations();
        repository.removeAll();
        distanceMatrix.clear();
    }
}
