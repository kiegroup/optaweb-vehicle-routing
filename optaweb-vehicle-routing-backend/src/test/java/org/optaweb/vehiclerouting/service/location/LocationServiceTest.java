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

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.Location;
import org.springframework.boot.context.event.ApplicationStartedEvent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
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

    @Mock
    ApplicationStartedEvent event;

    private final Coordinates coordinates = Coordinates.valueOf(0.0, 1.0);
    private final Location location = new Location(1, coordinates);

    private final List<Location> persistedLocations = Arrays.asList(location, location, location);

    @Before
    public void setUp() {
        when(repository.createLocation(any(Coordinates.class), anyString())).thenReturn(location);
        when(repository.removeLocation(location.id())).thenReturn(location);
        when(repository.locations()).thenReturn(persistedLocations);
    }

    @Test
    public void createLocation_should_validate_arguments() {
        assertThatNullPointerException().isThrownBy(() -> locationService.createLocation(null, "x"));
        assertThatNullPointerException().isThrownBy(() -> locationService.createLocation(coordinates, null));
    }

    @Test
    public void createLocation() {
        String description = "new location";
        assertThat(locationService.createLocation(coordinates, description)).isTrue();
        verify(repository).createLocation(coordinates, description);
        verify(distanceMatrix).addLocation(location);
        verify(optimizer).addLocation(eq(location), any(DistanceMatrix.class));
    }

    @Test
    public void removeLocation() {
        locationService.removeLocation(location.id());
        verify(repository).removeLocation(location.id());
        verify(optimizer).removeLocation(location);
        // TODO remove location from distance matrix
    }

    @Test
    public void clear() {
        locationService.clear();
        verify(optimizer).clear();
        verify(distanceMatrix).clear();
        verify(repository).removeAll();
    }

    @Test
    public void should_reload_on_startup() {
        locationService.reload(event);
        verify(repository).locations();
        verify(distanceMatrix, times(persistedLocations.size())).addLocation(location);
        verify(optimizer, times(persistedLocations.size())).addLocation(location, distanceMatrix);
    }

    @Test
    public void should_not_optimize_and_roll_back_if_distance_calculation_fails() {
        doThrow(RuntimeException.class).when(distanceMatrix).addLocation(any());
        assertThat(locationService.createLocation(coordinates, "")).isFalse();
        verifyZeroInteractions(optimizer);
        // roll back
        verify(repository).removeLocation(location.id());
    }
}
