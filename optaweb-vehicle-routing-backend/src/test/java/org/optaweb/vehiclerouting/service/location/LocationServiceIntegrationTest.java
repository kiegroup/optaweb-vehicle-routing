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
        locationService.addLocation(new Location(1000, Coordinates.of(-1, 12)));
        locationService.createLocation(Coordinates.of(12, -1), "location 1");
        Optional<Location> location = locationService.createLocation(Coordinates.of(32, -5), "location 2");
        assertThat(location).isNotEmpty();
        locationService.populateDistanceMatrix();
        locationService.removeLocation(location.get().id());
        locationService.removeAll();
    }
}
