package org.optaweb.vehiclerouting.plugin.rest;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.optaweb.vehiclerouting.service.demo.DemoService;

@Path("api/demo/{name}")
public class DemoResource {

    private final DemoService demoService;

    @Inject
    public DemoResource(DemoService demoService) {
        this.demoService = demoService;
    }

    /**
     * Load a demo data set.
     *
     * @param name data set name
     */
    @POST
    public void loadDemo(@PathParam("name") String name) {
        demoService.loadDemo(name);
    }

}
