package org.acme.getting.started;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.optaweb.vehiclerouting.service.error.ErrorEvent;

@Path("/hello")
@ApplicationScoped
public class GreetingResource {

    @Inject
    Event<ErrorEvent> errorEventEvent;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        errorEventEvent.fire(new ErrorEvent(this, "Hello RESTEasy"));
        return "Hello RESTEasy";
    }
}
