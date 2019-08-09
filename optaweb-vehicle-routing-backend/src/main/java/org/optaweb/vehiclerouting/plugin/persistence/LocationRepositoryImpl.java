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

package org.optaweb.vehiclerouting.plugin.persistence;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.service.location.LocationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class LocationRepositoryImpl implements LocationRepository {

    private static final Logger logger = LoggerFactory.getLogger(LocationRepositoryImpl.class);
    private final LocationCrudRepository repository;

    @Autowired
    LocationRepositoryImpl(LocationCrudRepository repository) {
        this.repository = repository;
    }

    @Override
    public Location createLocation(Coordinates coordinates, String description) {
        LocationEntity locationEntity = repository.save(
                new LocationEntity(coordinates.latitude(), coordinates.longitude(), description)
        );
        Location location = toDomain(locationEntity);
        logger.info("Created {}", location);
        return location;
    }

    @Override
    public List<Location> locations() {
        return StreamSupport.stream(repository.findAll().spliterator(), false)
                .map(LocationRepositoryImpl::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Location removeLocation(long id) {
        Optional<LocationEntity> maybeLocation = repository.findById(id);
        maybeLocation.ifPresent(locationEntity -> repository.deleteById(id));
        LocationEntity locationEntity = maybeLocation.orElseThrow(
                () -> new IllegalArgumentException("Location{id=" + id + "} doesn't exist")
        );
        Location location = toDomain(locationEntity);
        logger.info("Deleted {}", location);
        return location;
    }

    @Override
    public void removeAll() {
        repository.deleteAll();
    }

    @Override
    public Optional<Location> find(Long locationId) {
        return repository.findById(locationId).map(LocationRepositoryImpl::toDomain);
    }

    private static Location toDomain(LocationEntity locationEntity) {
        return new Location(
                locationEntity.getId(),
                new Coordinates(locationEntity.getLatitude(), locationEntity.getLongitude()),
                locationEntity.getDescription()
        );
    }
}
