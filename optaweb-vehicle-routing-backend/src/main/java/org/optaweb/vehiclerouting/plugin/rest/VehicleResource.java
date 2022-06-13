package org.optaweb.vehiclerouting.plugin.rest;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.optaweb.vehiclerouting.service.vehicle.VehicleService;

@Path("api/vehicle")
public class VehicleResource {

    private final VehicleService vehicleService;

    @Inject
    public VehicleResource(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @POST
    public void addVehicle() {
        vehicleService.createVehicle();
    }

    /**
     * Delete vehicle.
     *
     * @param id ID of the vehicle to be deleted
     */
    @DELETE
    @Path("{id}")
    public void removeVehicle(@PathParam("id") long id) {
        vehicleService.removeVehicle(id);
    }

    @POST
    @Path("deleteAny")
    public void removeAnyVehicle() {
        vehicleService.removeAnyVehicle();
    }

    @POST
    @Path("{id}/capacity")
    public void changeCapacity(@PathParam("id") long id, int capacity) {
        vehicleService.changeCapacity(id, capacity);
    }
}
