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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.service.error.ErrorEvent;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class LocationServiceTest {

    @Mock
    private LocationRepository repository;
    @Mock
    private RouteOptimizer optimizer;
    @Mock
    private DistanceMatrix distanceMatrix;
    @Mock
    private ApplicationEventPublisher eventPublisher;
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
    void createLocation(@Mock DistanceMatrixRow matrixRow) {
        String description = "new location";
        when(repository.createLocation(coordinates, description)).thenReturn(location);
        when(distanceMatrix.addLocation(any())).thenReturn(matrixRow);

        assertThat(locationService.createLocation(coordinates, description)).isTrue();

        verify(repository).createLocation(coordinates, description);
        verify(distanceMatrix).addLocation(location);
        verify(optimizer).addLocation(location, matrixRow);
    }

    @Test
    void addLocation_should_validate_arguments() {
        assertThatNullPointerException().isThrownBy(() -> locationService.addLocation(null));
    }

    @Test
    void addLocation(@Mock DistanceMatrixRow matrixRow) {
        when(distanceMatrix.addLocation(any())).thenReturn(matrixRow);
        assertThat(locationService.addLocation(location)).isTrue();

        verifyNoInteractions(repository);
        verify(distanceMatrix).addLocation(location);
        verify(optimizer).addLocation(location, matrixRow);
    }

    @Test
    void removing_depot_should_be_successful_when_it_is_the_last_location() {
        when(repository.locations()).thenReturn(Collections.singletonList(location));
        when(repository.find(location.id())).thenReturn(Optional.of(location));

        locationService.removeLocation(location.id());

        verify(repository).removeLocation(location.id());
        verify(optimizer).removeLocation(location);
        verifyNoInteractions(eventPublisher);
        // TODO remove location from distance matrix
    }

    @Test
    void removing_nonexistent_location_should_publish_error() {
        when(repository.find(location.id())).thenReturn(Optional.empty());

        locationService.removeLocation(location.id());

        verifyNoInteractions(optimizer);
        verify(repository, never()).removeLocation(anyLong());
        verify(eventPublisher).publishEvent(any(ErrorEvent.class));
    }

    @Test
    void removing_depot_when_there_are_other_locations_should_publish_error() {
        Location depot = new Location(1, coordinates);
        Location visit = new Location(2, coordinates);
        when(repository.locations()).thenReturn(Arrays.asList(depot, visit));
        when(repository.find(depot.id())).thenReturn(Optional.of(depot));

        locationService.removeLocation(depot.id());

        verifyNoInteractions(optimizer);
        verifyNoInteractions(distanceMatrix);
        verify(repository, never()).removeLocation(anyLong());
        verify(eventPublisher).publishEvent(any(ErrorEvent.class));
    }

    @Test
    void removing_visit_should_be_successful() {
        Location depot = new Location(1, coordinates);
        Location visit = new Location(2, coordinates);
        when(repository.locations()).thenReturn(Arrays.asList(depot, visit));
        when(repository.find(visit.id())).thenReturn(Optional.of(visit));

        locationService.removeLocation(visit.id());

        verify(optimizer).removeLocation(visit);
        verify(distanceMatrix).removeLocation(visit);
        verify(repository).removeLocation(visit.id());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void clear() {
        locationService.removeAll();
        verify(optimizer).removeAllLocations();
        verify(repository).removeAll();
        verify(distanceMatrix).clear();
    }

    @Test
    void should_not_optimize_and_roll_back_if_distance_calculation_fails() {
        when(repository.createLocation(coordinates, "")).thenReturn(location);
        doThrow(new RuntimeException("test exception")).when(distanceMatrix).addLocation(location);

        assertThat(locationService.createLocation(coordinates, "")).isFalse();
        verifyNoInteractions(optimizer);
        // publish error event
        verify(eventPublisher).publishEvent(any(ErrorEvent.class));
        // roll back
        verify(repository).removeLocation(location.id());
    }
}
