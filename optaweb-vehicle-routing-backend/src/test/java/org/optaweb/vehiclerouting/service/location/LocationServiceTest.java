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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import javax.enterprise.event.Event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.Distance;
import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.service.distance.DistanceRepository;
import org.optaweb.vehiclerouting.service.error.ErrorEvent;

@ExtendWith(MockitoExtension.class)
class LocationServiceTest {

    @Mock
    private LocationRepository repository;
    @Mock
    private DistanceRepository distanceRepository;
    @Mock
    private LocationPlanner planner;
    @Mock
    private DistanceMatrix distanceMatrix;
    @Mock
    private Event<ErrorEvent> errorEvent;
    @InjectMocks
    private LocationService locationService;

    private final Coordinates coordinates = Coordinates.of(0.0, 1.0);
    private final Location location = new Location(1, coordinates);

    @Test
    void createLocation_should_validate_arguments() {
        assertThatNullPointerException().isThrownBy(() -> locationService.createLocation(null, "x"));
        assertThatNullPointerException().isThrownBy(() -> locationService.createLocation(coordinates, null));
    }

    @Test
    void createLocation(@Mock DistanceMatrixRow matrixRow) {
        Distance distance = Distance.ofMillis(123);
        Location existingLocation = new Location(2, coordinates);
        when(repository.locations()).thenReturn(Arrays.asList(existingLocation));
        String description = "new location";
        when(repository.createLocation(coordinates, description)).thenReturn(location);
        when(distanceMatrix.addLocation(any())).thenReturn(matrixRow);
        when(distanceMatrix.distance(any(), any())).thenReturn(distance);
        when(matrixRow.distanceTo(anyLong())).thenReturn(distance);

        assertThat(locationService.createLocation(coordinates, description)).contains(location);

        verify(repository).createLocation(coordinates, description);
        verify(distanceMatrix).addLocation(location);
        verify(distanceRepository).saveDistance(existingLocation, location, distance);
        verify(distanceRepository).saveDistance(location, existingLocation, distance);
        verify(planner).addLocation(location, matrixRow);
    }

    @Test
    void addLocation_should_validate_arguments() {
        assertThatNullPointerException().isThrownBy(() -> locationService.addLocation(null));
    }

    @Test
    void addLocation(@Mock DistanceMatrixRow matrixRow) {
        when(distanceMatrix.addLocation(any())).thenReturn(matrixRow);

        locationService.addLocation(location);

        verifyNoInteractions(repository);
        verifyNoInteractions(distanceRepository);
        verify(distanceMatrix).addLocation(location);
        verify(planner).addLocation(location, matrixRow);
    }

    @Test
    void removing_depot_should_be_successful_when_it_is_the_last_location() {
        when(repository.locations()).thenReturn(Collections.singletonList(location));
        when(repository.find(location.id())).thenReturn(Optional.of(location));

        locationService.removeLocation(location.id());

        verify(repository).removeLocation(location.id());
        verify(distanceRepository).deleteDistances(location);
        verify(planner).removeLocation(location);
        verifyNoInteractions(errorEvent);
        // TODO remove location from distance matrix
    }

    @Test
    void removing_nonexistent_location_should_publish_error() {
        when(repository.find(location.id())).thenReturn(Optional.empty());

        locationService.removeLocation(location.id());

        verifyNoInteractions(planner);
        verify(repository, never()).removeLocation(anyLong());
        verify(distanceRepository, never()).deleteDistances(any(Location.class));
        verify(errorEvent).fire(any(ErrorEvent.class));
    }

    @Test
    void removing_depot_when_there_are_other_locations_should_publish_error() {
        Location depot = new Location(1, coordinates);
        Location visit = new Location(2, coordinates);
        when(repository.locations()).thenReturn(Arrays.asList(depot, visit));
        when(repository.find(depot.id())).thenReturn(Optional.of(depot));

        locationService.removeLocation(depot.id());

        verifyNoInteractions(planner);
        verifyNoInteractions(distanceMatrix);
        verify(repository, never()).removeLocation(anyLong());
        verify(distanceRepository, never()).deleteDistances(any(Location.class));
        verify(errorEvent).fire(any(ErrorEvent.class));
    }

    @Test
    void removing_visit_should_be_successful() {
        Location depot = new Location(1, coordinates);
        Location visit = new Location(2, coordinates);
        when(repository.locations()).thenReturn(Arrays.asList(depot, visit));
        when(repository.find(visit.id())).thenReturn(Optional.of(visit));

        locationService.removeLocation(visit.id());

        verify(planner).removeLocation(visit);
        verify(distanceMatrix).removeLocation(visit);
        verify(repository).removeLocation(visit.id());
        verify(distanceRepository).deleteDistances(visit);
        verifyNoInteractions(errorEvent);
    }

    @Test
    void clear() {
        locationService.removeAll();
        verify(planner).removeAllLocations();
        verify(repository).removeAll();
        verify(distanceRepository).deleteAll();
        verify(distanceMatrix).clear();
    }

    @Test
    void should_not_optimize_and_roll_back_if_distance_calculation_fails() {
        when(repository.createLocation(coordinates, "")).thenReturn(location);
        doThrow(new RuntimeException("test exception")).when(distanceMatrix).addLocation(location);

        assertThat(locationService.createLocation(coordinates, "")).isEmpty();
        verifyNoInteractions(planner);
        verifyNoInteractions(distanceRepository);
        // publish error event
        verify(errorEvent).fire(any(ErrorEvent.class));
        // roll back
        verify(repository).removeLocation(location.id());
    }

    @Test
    void populate_matrix_should_read_all_distances() {
        Location depot = new Location(1, coordinates);
        Location visit1 = new Location(2, coordinates);
        Location visit2 = new Location(3, coordinates);
        when(repository.locations()).thenReturn(Arrays.asList(depot, visit1, visit2));
        when(distanceRepository.getDistance(any(Location.class), any(Location.class))).thenReturn(Optional.of(Distance.ZERO));

        locationService.populateDistanceMatrix();

        verify(distanceRepository, times(6)).getDistance(any(Location.class), any(Location.class));
        verify(distanceMatrix, times(6)).put(any(Location.class), any(Location.class), any(Distance.class));
    }
}
