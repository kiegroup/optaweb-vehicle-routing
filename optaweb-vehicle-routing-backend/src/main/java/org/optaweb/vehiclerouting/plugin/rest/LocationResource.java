package org.optaweb.vehiclerouting.plugin.rest;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.plugin.rest.model.PortableLocation;
import org.optaweb.vehiclerouting.service.location.LocationService;

@Path("api/location")
public class LocationResource {

    private final LocationService locationService;

    @Inject
    public LocationResource(LocationService locationService) {
        this.locationService = locationService;
    }

    /**
     * Create new location.
     *
     * @param request new location description
     */
    @Transactional
    @POST
    public void addLocation(PortableLocation request) {
        locationService.createLocation(
                new Coordinates(request.getLatitude(), request.getLongitude()),
                request.getDescription());
    }

    /**
     * Delete location.
     *
     * @param id ID of the location to be deleted
     */
    @Transactional
    @DELETE
    @Path("{id}")
    public void deleteLocation(@PathParam("id") long id) {
        locationService.removeLocation(id);
    }
}
