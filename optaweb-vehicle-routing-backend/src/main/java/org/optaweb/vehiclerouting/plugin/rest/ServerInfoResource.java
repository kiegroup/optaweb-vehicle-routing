package org.optaweb.vehiclerouting.plugin.rest;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.optaweb.vehiclerouting.plugin.rest.model.PortableCoordinates;
import org.optaweb.vehiclerouting.plugin.rest.model.RoutingProblemInfo;
import org.optaweb.vehiclerouting.plugin.rest.model.ServerInfo;
import org.optaweb.vehiclerouting.service.demo.DemoService;
import org.optaweb.vehiclerouting.service.region.BoundingBox;
import org.optaweb.vehiclerouting.service.region.RegionService;

@Path("api/serverInfo")
public class ServerInfoResource {

    private final DemoService demoService;
    private final RegionService regionService;

    @Inject
    public ServerInfoResource(DemoService demoService, RegionService regionService) {
        this.demoService = demoService;
        this.regionService = regionService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ServerInfo serverInfo() {
        BoundingBox boundingBox = regionService.boundingBox();
        List<PortableCoordinates> portableBoundingBox = Arrays.asList(
                PortableCoordinates.fromCoordinates(boundingBox.getSouthWest()),
                PortableCoordinates.fromCoordinates(boundingBox.getNorthEast()));
        List<RoutingProblemInfo> demos = demoService.demos().stream()
                .map(routingProblem -> new RoutingProblemInfo(
                        routingProblem.name(),
                        routingProblem.visits().size()))
                .collect(toList());
        return new ServerInfo(portableBoundingBox, regionService.countryCodes(), demos);
    }
}
