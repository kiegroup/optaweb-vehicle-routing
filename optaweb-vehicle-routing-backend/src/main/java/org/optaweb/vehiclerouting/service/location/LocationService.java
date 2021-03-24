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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.service.distance.DistanceRepository;
import org.optaweb.vehiclerouting.service.error.ErrorEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Performs location-related use cases.
 */
@ApplicationScoped
public class LocationService {

    private static final Logger logger = LoggerFactory.getLogger(LocationService.class);

    private final LocationRepository repository;
    private final DistanceRepository distanceRepository;
    private final LocationPlanner planner; // TODO move to RoutingPlanService (SRP)
    private final DistanceMatrix distanceMatrix;
    private final Event<ErrorEvent> errorEvent;

    @Inject
    LocationService(
            LocationRepository repository,
            DistanceRepository distanceRepository,
            LocationPlanner planner,
            DistanceMatrix distanceMatrix,
            Event<ErrorEvent> errorEvent) {
        this.repository = repository;
        this.distanceRepository = distanceRepository;
        this.planner = planner;
        this.distanceMatrix = distanceMatrix;
        this.errorEvent = errorEvent;
    }

    public synchronized void addLocation(Location location) {
        Objects.requireNonNull(location);
        DistanceMatrixRow distanceMatrixRow = distanceMatrix.addLocation(location);
        planner.addLocation(location, distanceMatrixRow);
    }

    @Transactional
    public synchronized Optional<Location> createLocation(Coordinates coordinates, String description) {
        Objects.requireNonNull(coordinates);
        Objects.requireNonNull(description);
        // TODO if (router.isLocationAvailable(coordinates))
        Location location = repository.createLocation(coordinates, description);
        Optional<DistanceMatrixRow> distanceMatrixRow = addToMatrix(location);
        if (distanceMatrixRow.isPresent()) {
            planner.addLocation(location, distanceMatrixRow.get());
            return Optional.of(location);
        } else {
            repository.removeLocation(location.id());
            return Optional.empty();
        }
    }

    private Optional<DistanceMatrixRow> addToMatrix(Location location) {
        try {
            DistanceMatrixRow distanceMatrixRow = distanceMatrix.addLocation(location);
            repository.locations().stream()
                    .filter(existingLocation -> !existingLocation.equals(location))
                    .forEach(existingLocation -> {
                        distanceRepository.saveDistance(location, existingLocation,
                                distanceMatrixRow.distanceTo(existingLocation.id()));
                        distanceRepository.saveDistance(existingLocation, location,
                                distanceMatrix.distance(existingLocation, location));
                    });
            return Optional.of(distanceMatrixRow);
        } catch (Exception e) {
            logger.error(
                    "Failed to calculate distances for location {}, it will be discarded",
                    location.fullDescription(), e);
            errorEvent.fire(new ErrorEvent(
                    this,
                    "Failed to calculate distances for location " + location.fullDescription()
                            + ", it will be discarded.\n" + e.toString()));
            return Optional.empty();
        }
    }

    @Transactional
    public synchronized void removeLocation(long id) {
        Optional<Location> optionalLocation = repository.find(id);
        if (!optionalLocation.isPresent()) {
            errorEvent.fire(new ErrorEvent(this, "Location [" + id + "] cannot be removed because it doesn't exist."));
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
                errorEvent.fire(new ErrorEvent(this, "You can only remove depot if there are no visits."));
                return;
            }
        }

        planner.removeLocation(removedLocation);
        repository.removeLocation(id);
        distanceMatrix.removeLocation(removedLocation);
        distanceRepository.deleteDistances(removedLocation);
    }

    @Transactional
    public synchronized void removeAll() {
        planner.removeAllLocations();
        repository.removeAll();
        distanceMatrix.clear();
        distanceRepository.deleteAll();
    }

    public void populateDistanceMatrix() {
        repository.locations()
                .forEach(from -> repository.locations().stream()
                        .filter(to -> !to.equals(from))
                        .forEach(to -> distanceMatrix.put(from, to, distanceRepository.getDistance(from, to)
                                .orElseThrow(() -> new IllegalStateException("Distance from: [" + from + "] to: [" + to
                                        + "] is missing in the distance repository. This should not happen.")))));
    }
}
