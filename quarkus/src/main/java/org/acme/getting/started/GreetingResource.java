package org.acme.getting.started;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.domain.RoutingProblem;
import org.optaweb.vehiclerouting.service.demo.DemoService;
import org.optaweb.vehiclerouting.service.error.ErrorEvent;
import org.optaweb.vehiclerouting.service.location.DistanceMatrix;
import org.optaweb.vehiclerouting.service.location.DistanceMatrixRow;
import org.optaweb.vehiclerouting.service.location.LocationService;
import org.optaweb.vehiclerouting.service.region.RegionService;
import org.optaweb.vehiclerouting.service.vehicle.VehicleService;

@Path("/hello")
@ApplicationScoped
public class GreetingResource {

    @Inject
    Event<ErrorEvent> errorEventEvent;
    @Inject
    VehicleService vehicleService;
    @Inject
    LocationService locationService;
    @Inject
    DemoService demoService;
    @Inject
    DistanceMatrix distanceMatrix;
    @Inject
    RegionService regionService;

    @Transactional
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        Collection<RoutingProblem> demos = demoService.demos();
        errorEventEvent.fire(new ErrorEvent(this, demos.stream().map(Objects::toString).collect(Collectors.joining(","))));

        List<String> countryCodes = regionService.countryCodes();
        errorEventEvent.fire(new ErrorEvent(this, countryCodes.toString()));

        vehicleService.createVehicle();
        vehicleService.createVehicle();
        vehicleService.createVehicle();
        vehicleService.removeAll();

        locationService.createLocation(Coordinates.valueOf(12.3, 88.8), "TEST");

        DistanceMatrixRow row = distanceMatrix.addLocation(new Location(999999, Coordinates.valueOf(1, 1)));
        errorEventEvent.fire(new ErrorEvent(this, "Distance: " + row.distanceTo(4)));
        return "Hello RESTEasy";
    }
}
