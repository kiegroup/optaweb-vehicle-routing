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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.Location;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocationServiceTest {

    @Mock
    private LocationRepository repository;
    @Mock
    private RouteOptimizer optimizer;
    @Mock
    private DistanceMatrix distanceMatrix;
    @InjectMocks
    private LocationService locationService;

    private final Coordinates coordinates = Coordinates.valueOf(0.0, 1.0);
    private final Location location = new Location(1, coordinates);

    @Test
    void createLocation_should_validate_arguments() {
        assertThatNullPointerException().isThrownBy(() -> locationService.createLocation(null, "x"));
        assertThatNullPointerException().isThrownBy(() -> locationService.createLocation(coordinates, null));
    }

    @Test
    void createLocation() {
        String description = "new location";
        when(repository.createLocation(coordinates, description)).thenReturn(location);

        assertThat(locationService.createLocation(coordinates, description)).isTrue();

        verify(repository).createLocation(coordinates, description);
        verify(distanceMatrix).addLocation(location);
        verify(optimizer).addLocation(location, distanceMatrix);
    }

    @Test
    void addLocation_should_validate_arguments() {
        assertThatNullPointerException().isThrownBy(() -> locationService.addLocation(null));
    }

    @Test
    void addLocation() {
        assertThat(locationService.addLocation(location)).isTrue();

        verifyNoInteractions(repository);
        verify(distanceMatrix).addLocation(location);
        verify(optimizer).addLocation(location, distanceMatrix);
    }

    @Test
    void removeLocation() {
        when(repository.removeLocation(location.id())).thenReturn(location);

        locationService.removeLocation(location.id());

        verify(repository).removeLocation(location.id());
        verify(optimizer).removeLocation(location);
        // TODO remove location from distance matrix
    }

    @Test
    void clear() {
        locationService.removeAll();
        verify(optimizer).removeAllLocations();
        verify(distanceMatrix).clear();
        verify(repository).removeAll();
    }

    @Test
    void should_not_optimize_and_roll_back_if_distance_calculation_fails() {
        when(repository.createLocation(coordinates, "")).thenReturn(location);
        doThrow(RuntimeException.class).when(distanceMatrix).addLocation(location);

        assertThat(locationService.createLocation(coordinates, "")).isFalse();
        verifyNoInteractions(optimizer);
        // roll back
        verify(repository).removeLocation(location.id());
    }
}
