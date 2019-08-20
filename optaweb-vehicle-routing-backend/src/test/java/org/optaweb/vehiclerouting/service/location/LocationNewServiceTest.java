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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.LocationNew;
import org.springframework.boot.context.event.ApplicationStartedEvent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocationNewServiceTest {

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
    private final LocationNew locationNew = new LocationNew(1, coordinates);

    private final List<LocationNew> persistedLocationNews = Arrays.asList(locationNew, locationNew, locationNew);

    @Test
    void createLocation_should_validate_arguments() {
        assertThatNullPointerException().isThrownBy(() -> locationService.createLocation(null, "x"));
        assertThatNullPointerException().isThrownBy(() -> locationService.createLocation(coordinates, null));
    }

    @Test
    void createLocation() {
        String description = "new location";
        when(repository.createLocation(coordinates, description)).thenReturn(locationNew);

        assertThat(locationService.createLocation(coordinates, description)).isTrue();

        verify(repository).createLocation(coordinates, description);
        verify(distanceMatrix).addLocation(locationNew);
        verify(optimizer).addLocation(eq(locationNew), any(DistanceMatrix.class));
    }

    @Test
    void removeLocation() {
        when(repository.removeLocation(locationNew.id())).thenReturn(locationNew);

        locationService.removeLocation(locationNew.id());

        verify(repository).removeLocation(locationNew.id());
        verify(optimizer).removeLocation(locationNew);
        // TODO remove location from distance matrix
    }

    @Test
    void clear() {
        locationService.clear();
        verify(optimizer).clear();
        verify(distanceMatrix).clear();
        verify(repository).removeAll();
    }

    @Test
    void should_reload_on_startup() {
        when(repository.locations()).thenReturn(persistedLocationNews);

        locationService.reload(event);

        verify(repository).locations();
        verify(distanceMatrix, times(persistedLocationNews.size())).addLocation(locationNew);
        verify(optimizer, times(persistedLocationNews.size())).addLocation(locationNew, distanceMatrix);
    }

    @Test
    void should_not_optimize_and_roll_back_if_distance_calculation_fails() {
        when(repository.createLocation(coordinates, "")).thenReturn(locationNew);
        doThrow(RuntimeException.class).when(distanceMatrix).addLocation(any());

        assertThat(locationService.createLocation(coordinates, "")).isFalse();
        verifyZeroInteractions(optimizer);
        // roll back
        verify(repository).removeLocation(locationNew.id());
    }
}
