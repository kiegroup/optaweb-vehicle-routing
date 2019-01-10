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

package org.optaweb.vehiclerouting.service.location;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.optaweb.vehiclerouting.domain.LatLng;
import org.optaweb.vehiclerouting.domain.Location;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LocationServiceTest {

    @Mock
    private LocationRepository repository;
    @Mock
    private RouteOptimizer optimizer;
    @Mock
    private DistanceMatrix distanceMatrix;
    @InjectMocks
    private LocationService locationService;

    private final LatLng latLng = LatLng.valueOf(0.0, 1.0);
    private final Location location = new Location(1, latLng);

    @Before
    public void setUp() {
        when(repository.createLocation(any(LatLng.class))).thenReturn(location);
        when(repository.removeLocation(location.getId())).thenReturn(location);
    }

    @Test
    public void addLocation() {
        locationService.addLocation(latLng);
        verify(repository).createLocation(latLng);
        verify(distanceMatrix).addLocation(location);
        verify(optimizer).addLocation(eq(location), any(DistanceMatrix.class));
    }

    @Test
    public void removeLocation() {
        locationService.removeLocation(location.getId());
        verify(repository).removeLocation(location.getId());
        verify(optimizer).removeLocation(location);
        // TODO remove location from distance matrix
    }

    @Test
    public void loadDemo() {
        final int CITY_COUNT = Belgium.values().length;
        locationService.loadDemo();
        verify(repository, times(CITY_COUNT)).createLocation(any(LatLng.class));
        verify(distanceMatrix, times(CITY_COUNT)).addLocation(any(Location.class));
        verify(optimizer, times(CITY_COUNT)).addLocation(any(Location.class), any(DistanceMatrix.class));
    }

    @Test
    public void clear() {
        locationService.clear();
        verify(optimizer).clear();
        verify(distanceMatrix).clear();
        verify(repository).removeAll();
    }
}
