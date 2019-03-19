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

import org.optaweb.vehiclerouting.domain.LatLng;
import org.optaweb.vehiclerouting.domain.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Performs location-related use cases.
 */
@Service
public class LocationService {

    private static final Logger logger = LoggerFactory.getLogger(LocationService.class);

    private final LocationRepository repository;
    private final RouteOptimizer optimizer;
    private final DistanceMatrix distanceMatrix;

    public LocationService(LocationRepository repository,
                           RouteOptimizer optimizer,
                           DistanceMatrix distanceMatrix) {
        this.repository = repository;
        this.optimizer = optimizer;
        this.distanceMatrix = distanceMatrix;
    }

    @EventListener
    public synchronized void reload(ApplicationStartedEvent event) {
        repository.locations().forEach(this::submitToPlanner);
    }

    public synchronized boolean createLocation(LatLng latLng) {
        // TODO if (router.isLocationAvailable(latLng))
        return submitToPlanner(repository.createLocation(latLng));
    }

    private boolean submitToPlanner(Location location) {
        try {
            distanceMatrix.addLocation(location);
        } catch (Exception e) {
            // TODO relay the error event to the client
            logger.warn("Failed to calculate distances for {}, it will be discarded", location);
            logger.debug("Details:", e);
            repository.removeLocation(location.getId());
            return false; // do not proceed to optimizer
        }
        optimizer.addLocation(location, distanceMatrix);
        return true;
    }

    public synchronized void removeLocation(long id) {
        Location location = repository.removeLocation(id);
        optimizer.removeLocation(location);
    }

    public synchronized void clear() {
        optimizer.clear();
        repository.removeAll();
        distanceMatrix.clear();
    }
}
