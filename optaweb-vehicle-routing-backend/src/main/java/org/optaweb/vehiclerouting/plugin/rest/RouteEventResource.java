/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaweb.vehiclerouting.plugin.rest;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventSink;

import org.optaweb.vehiclerouting.domain.RoutingPlan;
import org.optaweb.vehiclerouting.plugin.rest.model.PortableErrorMessage;
import org.optaweb.vehiclerouting.plugin.rest.model.PortableRoutingPlanFactory;
import org.optaweb.vehiclerouting.service.error.ErrorMessage;
import org.optaweb.vehiclerouting.service.route.RouteListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@Path("api/events")
public class RouteEventResource {

    private static final Logger logger = LoggerFactory.getLogger(RouteEventResource.class);

    // TODO repository, not listener (service)
    private final RouteListener routeListener;

    private SseBroadcaster sseBroadcaster;
    private OutboundSseEvent.Builder eventBuilder;

    @Inject
    public RouteEventResource(RouteListener routeListener) {
        this.routeListener = routeListener;
    }

    // Handy during development.
    @PreDestroy
    public void closeBroadcaster() {
        if (sseBroadcaster != null) {
            logger.debug("Closing Server-Sent Events broadcaster.");
            sseBroadcaster.close();
        }
    }

    public void observeRoute(@Observes RoutingPlan event) {
        if (sseBroadcaster != null) {
            sseBroadcaster.broadcast(eventBuilder
                    .data(PortableRoutingPlanFactory.fromRoutingPlan(event))
                    .name("route")
                    .comment("route update")
                    .build());
        }
    }

    public void observeError(@Observes ErrorMessage event) {
        if (sseBroadcaster != null) {
            sseBroadcaster.broadcast(eventBuilder
                    .data(PortableErrorMessage.fromMessage(event))
                    .name("errorMessage")
                    .comment("error message")
                    .build());
        }
    }

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void sse(@Context Sse sse, @Context SseEventSink eventSink) {
        if (sseBroadcaster == null) {
            sseBroadcaster = sse.newBroadcaster();
            eventBuilder = sse.newEventBuilder()
                    .mediaType(MediaType.APPLICATION_JSON_TYPE)
                    .reconnectDelay(3000);
        }
        OutboundSseEvent sseEvent = eventBuilder
                .data(PortableRoutingPlanFactory.fromRoutingPlan(routeListener.getBestRoutingPlan()))
                .comment("best route")
                .build();
        eventSink.send(sseEvent);
        sseBroadcaster.register(eventSink);
    }
}
