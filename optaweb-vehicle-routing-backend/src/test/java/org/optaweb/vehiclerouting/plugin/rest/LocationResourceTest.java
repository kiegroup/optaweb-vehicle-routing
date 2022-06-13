package org.optaweb.vehiclerouting.plugin.rest;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.plugin.rest.model.PortableLocation;
import org.optaweb.vehiclerouting.service.location.LocationService;

@ExtendWith(MockitoExtension.class)
class LocationResourceTest {

    @Mock
    private LocationService locationService;
    @InjectMocks
    private LocationResource locationResource;

    @Test
    void addLocation() {
        Coordinates coords = Coordinates.of(0.0, 1.0);
        String description = "new location";
        PortableLocation request = new PortableLocation(321, coords.latitude(), coords.longitude(), description);
        locationResource.addLocation(request);
        verify(locationService).createLocation(coords, description);
    }

    @Test
    void removeLocation() {
        locationResource.deleteLocation(9L);
        verify(locationService).removeLocation(9);
    }
}
