/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.optaweb.vehiclerouting.service.reload;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.optaweb.vehiclerouting.service.location.LocationRepository;
import org.optaweb.vehiclerouting.service.location.LocationService;
import org.optaweb.vehiclerouting.service.vehicle.VehicleRepository;
import org.optaweb.vehiclerouting.service.vehicle.VehicleService;

import io.quarkus.runtime.StartupEvent;

/**
 * Reloads data from repositories when the application starts.
 */
@ApplicationScoped
public class ReloadService {

    private final VehicleRepository vehicleRepository;
    private final VehicleService vehicleService;
    private final LocationRepository locationRepository;
    private final LocationService locationService;

    @Inject
    ReloadService(
            VehicleRepository vehicleRepository,
            VehicleService vehicleService,
            LocationRepository locationRepository,
            LocationService locationService) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleService = vehicleService;
        this.locationRepository = locationRepository;
        this.locationService = locationService;
    }

    public void reload(@Observes StartupEvent startupEvent) {
        vehicleRepository.vehicles().forEach(vehicleService::addVehicle);
        locationService.populateDistanceMatrix();
        locationRepository.locations().forEach(locationService::addLocation);
    }
}
