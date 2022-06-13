package org.optaweb.vehiclerouting.plugin.rest;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.optaweb.vehiclerouting.service.vehicle.VehicleService;

@ExtendWith(MockitoExtension.class)
class VehicleResourceTest {

    @Mock
    private VehicleService vehicleService;
    @InjectMocks
    private VehicleResource vehicleResource;

    @Test
    void addVehicle() {
        vehicleResource.addVehicle();
        verify(vehicleService).createVehicle();
    }

    @Test
    void removeVehicle() {
        vehicleResource.removeVehicle(11L);
        verify(vehicleService).removeVehicle(11);
    }

    @Test
    void removeAnyVehicle() {
        vehicleResource.removeAnyVehicle();
        verify(vehicleService).removeAnyVehicle();
    }

    @Test
    void changeCapacity() {
        long vehicleId = 2000;
        int capacity = 50;
        vehicleResource.changeCapacity(vehicleId, capacity);
        verify(vehicleService).changeCapacity(vehicleId, capacity);
    }
}
