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

import org.optaweb.vehiclerouting.service.location.LocationRepository;
import org.optaweb.vehiclerouting.service.location.LocationService;
import org.optaweb.vehiclerouting.service.vehicle.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class ReloadService {

    private final VehicleService vehicleService;
    private final LocationRepository locationRepository;
    private final LocationService locationService;

    @Autowired
    ReloadService(
            VehicleService vehicleService,
            LocationRepository locationRepository,
            LocationService locationService
    ) {
        this.vehicleService = vehicleService;
        this.locationRepository = locationRepository;
        this.locationService = locationService;
    }

    @EventListener
    public synchronized void reload(ApplicationStartedEvent event) {
        for (int i = 0; i < 6; i++) {
            vehicleService.addVehicle();
        }
        locationRepository.locations().forEach(locationService::addLocation);
    }
}
