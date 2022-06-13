package org.optaweb.vehiclerouting.service.reload;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.domain.Vehicle;
import org.optaweb.vehiclerouting.domain.VehicleFactory;
import org.optaweb.vehiclerouting.service.location.LocationRepository;
import org.optaweb.vehiclerouting.service.location.LocationService;
import org.optaweb.vehiclerouting.service.vehicle.VehicleRepository;
import org.optaweb.vehiclerouting.service.vehicle.VehicleService;

import io.quarkus.runtime.StartupEvent;

@ExtendWith(MockitoExtension.class)
class ReloadServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;
    @Mock
    private VehicleService vehicleService;
    @Mock
    private LocationRepository locationRepository;
    @Mock
    private LocationService locationService;
    @InjectMocks
    private ReloadService reloadService;

    @Mock
    StartupEvent event;

    private final Vehicle vehicle = VehicleFactory.createVehicle(193, "Vehicle 193", 100);
    private final List<Vehicle> persistedVehicles = Arrays.asList(vehicle, vehicle);
    private final Location location = new Location(1, Coordinates.of(0.0, 1.0));
    private final List<Location> persistedLocations = Arrays.asList(location, location, location);

    @Test
    void should_reload_on_startup() {
        when(vehicleRepository.vehicles()).thenReturn(persistedVehicles);
        when(locationRepository.locations()).thenReturn(persistedLocations);

        reloadService.reload(event);

        verify(vehicleRepository).vehicles();
        verify(vehicleService, times(persistedVehicles.size())).addVehicle(vehicle);
        verify(locationRepository).locations();
        verify(locationService, times(persistedLocations.size())).addLocation(location);
        verify(locationService).populateDistanceMatrix();
    }
}
