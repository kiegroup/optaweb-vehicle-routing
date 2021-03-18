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

package org.optaweb.vehiclerouting.service.location;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.Distance;
import org.optaweb.vehiclerouting.domain.Location;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;

@QuarkusTest
class LocationServiceIntegrationTest {

    @InjectMock
    DistanceMatrix distanceMatrix;
    @Inject
    LocationService locationService;

    @Test
    void location_service_should_be_transactional() {
        when(distanceMatrix.addLocation(any())).thenReturn(locationId -> Distance.ZERO);
        when(distanceMatrix.distance(any(), any())).thenReturn(Distance.ZERO);
        locationService.addLocation(new Location(1000, Coordinates.valueOf(-1, 12)));
        locationService.createLocation(Coordinates.valueOf(12, -1), "location 1");
        Optional<Location> location = locationService.createLocation(Coordinates.valueOf(32, -5), "location 2");
        assertThat(location).isNotEmpty();
        locationService.populateDistanceMatrix();
        locationService.removeLocation(location.get().id());
        locationService.removeAll();
    }
}
