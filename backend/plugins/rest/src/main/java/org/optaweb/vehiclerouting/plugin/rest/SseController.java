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
import org.optaweb.vehiclerouting.service.route.RouteListener;

@ApplicationScoped
@Path("/events")
public class SseController {

    // TODO repository, not listener (service)
    @Inject
    RouteListener routeListener;

    private SseBroadcaster sseBroadcaster;
    private OutboundSseEvent.Builder eventBuilder;

    // Handy during development.
    @PreDestroy
    public void closeBroadcaster() {
        sseBroadcaster.close();
    }

    public void observeRoute(@Observes RoutingPlan event) {
        if (sseBroadcaster != null) {
            sseBroadcaster.broadcast(eventBuilder
                    .data(PortableRoutingPlanFactory.fromRoutingPlan(event))
                    .comment("route update")
                    .build());
        }
    }

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @Path("route")
    public void sse(@Context Sse sse, @Context SseEventSink eventSink) {
        if (sseBroadcaster == null) {
            sseBroadcaster = sse.newBroadcaster();
            eventBuilder = sse.newEventBuilder()
                    .name("route")
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
