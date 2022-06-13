package org.optaweb.vehiclerouting.plugin.rest;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.optaweb.vehiclerouting.service.location.LocationService;
import org.optaweb.vehiclerouting.service.vehicle.VehicleService;

@ExtendWith(MockitoExtension.class)
class ClearResourceTest {

    @Mock
    private LocationService locationService;
    @Mock
    private VehicleService vehicleService;
    @InjectMocks
    private ClearResource clearResource;

    @Test
    void clear() {
        clearResource.clear();
        verify(locationService).removeAll();
        verify(vehicleService).removeAll();
    }
}
