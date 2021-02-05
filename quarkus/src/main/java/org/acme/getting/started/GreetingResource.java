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

import org.optaweb.vehiclerouting.domain.RoutingProblem;
import org.optaweb.vehiclerouting.service.demo.DemoService;
import org.optaweb.vehiclerouting.service.error.ErrorEvent;
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
    RegionService regionService;

    @Transactional
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        Collection<RoutingProblem> demos = demoService.demos();
        errorEventEvent.fire(new ErrorEvent(this, demos.stream().map(Objects::toString).collect(Collectors.joining(","))));

        List<String> countryCodes = regionService.countryCodes();
        errorEventEvent.fire(new ErrorEvent(this, countryCodes.toString()));

        locationService.removeAll();
        vehicleService.removeAll();

        demoService.loadDemo(demos.iterator().next().name());

        return "Hello RESTEasy";
    }
}
