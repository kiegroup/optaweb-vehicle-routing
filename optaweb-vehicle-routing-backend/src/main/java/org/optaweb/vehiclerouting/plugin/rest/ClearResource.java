package org.optaweb.vehiclerouting.plugin.rest;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.optaweb.vehiclerouting.service.location.LocationService;
import org.optaweb.vehiclerouting.service.vehicle.VehicleService;

@Path("api/clear")
public class ClearResource {

    private final LocationService locationService;
    private final VehicleService vehicleService;

    @Inject
    public ClearResource(LocationService locationService, VehicleService vehicleService) {
        this.locationService = locationService;
        this.vehicleService = vehicleService;
    }

    @POST
    public void clear() {
        // TODO do this in one step (=> new RoutingPlanService)
        vehicleService.removeAll();
        locationService.removeAll();
    }
}
