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

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.domain.Vehicle;
import org.optaweb.vehiclerouting.domain.VehicleFactory;
import org.optaweb.vehiclerouting.service.location.LocationRepository;
import org.optaweb.vehiclerouting.service.location.LocationService;
import org.optaweb.vehiclerouting.service.vehicle.VehicleRepository;
import org.optaweb.vehiclerouting.service.vehicle.VehicleService;

import io.quarkus.runtime.StartupEvent;

@ExtendWith(MockitoExtension.class)
class ReloadServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;
    @Mock
    private VehicleService vehicleService;
    @Mock
    private LocationRepository locationRepository;
    @Mock
    private LocationService locationService;
    @InjectMocks
    private ReloadService reloadService;

    @Mock
    StartupEvent event;

    private final Vehicle vehicle = VehicleFactory.createVehicle(193, "Vehicle 193", 100);
    private final List<Vehicle> persistedVehicles = Arrays.asList(vehicle, vehicle);
    private final Coordinates coordinates = Coordinates.valueOf(0.0, 1.0);
    private final Location location = new Location(1, coordinates);
    private final List<Location> persistedLocations = Arrays.asList(location, location, location);

    @Test
    void should_reload_on_startup() {
        when(vehicleRepository.vehicles()).thenReturn(persistedVehicles);
        when(locationRepository.locations()).thenReturn(persistedLocations);

        reloadService.reload(event);

        verify(vehicleRepository).vehicles();
        verify(vehicleService, times(persistedVehicles.size())).addVehicle(vehicle);
        verify(locationRepository).locations();
        verify(locationService, times(persistedLocations.size())).addLocation(location);
        verify(locationService).populateDistanceMatrix();
    }
}
