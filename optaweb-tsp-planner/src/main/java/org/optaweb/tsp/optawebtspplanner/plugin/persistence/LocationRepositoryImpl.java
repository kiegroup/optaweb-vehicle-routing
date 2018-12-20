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

package org.optaweb.tsp.optawebtspplanner.plugin.persistence;

import java.util.Optional;

import org.optaweb.tsp.optawebtspplanner.core.LatLng;
import org.optaweb.tsp.optawebtspplanner.core.Location;
import org.optaweb.tsp.optawebtspplanner.interactor.location.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LocationRepositoryImpl implements LocationRepository {

    private final LocationCrudRepository repository;

    @Autowired
    public LocationRepositoryImpl(LocationCrudRepository repository) {
        this.repository = repository;
    }

    @Override
    public Location createLocation(LatLng latLng) {
        LocationEntity locationEntity = repository.save(new LocationEntity(latLng.getLatitude(), latLng.getLongitude()));
        return new Location(locationEntity.getId(), latLng);
    }

    @Override
    public Location removeLocation(long id) {
        Optional<LocationEntity> maybeLocation = repository.findById(id);
        maybeLocation.ifPresent(locationEntity -> repository.deleteById(id));
        LocationEntity locationEntity = maybeLocation.orElseThrow(IllegalArgumentException::new);
        return new Location(locationEntity.getId(),
                new LatLng(locationEntity.getLatitude(), locationEntity.getLongitude()));
    }
}
