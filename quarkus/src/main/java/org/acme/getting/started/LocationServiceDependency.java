/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.acme.getting.started;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.service.location.DistanceMatrixRow;
import org.optaweb.vehiclerouting.service.location.LocationPlanner;
import org.optaweb.vehiclerouting.service.location.LocationRepository;

@ApplicationScoped
public class LocationServiceDependency implements LocationRepository, LocationPlanner {

    @Override
    public void addLocation(Location location, DistanceMatrixRow distanceMatrixRow) {

    }

    @Override
    public void removeLocation(Location location) {

    }

    @Override
    public void removeAllLocations() {

    }

    @Override
    public Location createLocation(Coordinates coordinates, String description) {
        return null;
    }

    @Override
    public List<Location> locations() {
        return null;
    }

    @Override
    public Location removeLocation(long id) {
        return null;
    }

    @Override
    public void removeAll() {

    }

    @Override
    public Optional<Location> find(long locationId) {
        return Optional.of(new Location(locationId, Coordinates.valueOf(999, 999), "Fake location"));
    }
}
