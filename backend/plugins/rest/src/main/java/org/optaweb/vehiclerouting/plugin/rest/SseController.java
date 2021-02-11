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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;

import org.jboss.resteasy.reactive.RestSseElementType;
import org.optaweb.vehiclerouting.domain.RoutingPlan;
import org.optaweb.vehiclerouting.service.route.RouteListener;
import org.optaweb.vehiclerouting.service.route.RoutingPlanConsumer;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.operators.multi.processors.BroadcastProcessor;

@ApplicationScoped
@Path("/events")
public class SseController implements RoutingPlanConsumer {

    // TODO repository, not listener (service)
    @Inject
    RouteListener routeListener;

    private final BroadcastProcessor<RoutingPlan> processor = BroadcastProcessor.create();
    private final Multi<PortableRoutingPlan> multi = processor
            .onItem().transform(PortableRoutingPlanFactory::fromRoutingPlan)
            .onFailure().recoverWithItem(() -> {
                throw new IllegalArgumentException("FIXME");
            });

    @Override
    public void consumePlan(RoutingPlan routingPlan) {
        processor.onNext(routingPlan);
    }

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @RestSseElementType(MediaType.APPLICATION_JSON)
    @Path("route")
    public Multi<PortableRoutingPlan> route() {
        // TODO return current best plan immediately or let the client fetch the best plan upon a successful connection
        return multi;
    }

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @Path("route2")
    public void sse(@Context Sse sse, @Context SseEventSink eventSink) {
        OutboundSseEvent.Builder eventBuilder = sse.newEventBuilder();
        OutboundSseEvent sseEvent = eventBuilder
                .name("route")
                .mediaType(MediaType.APPLICATION_JSON_TYPE)
                .data(routeListener.getBestRoutingPlan())
                .reconnectDelay(3000)
                .comment("best route")
                .build();
        eventSink.send(sseEvent);
        multi.subscribe().with(item -> eventSink.send(eventBuilder
                .name("route")
                .mediaType(MediaType.APPLICATION_JSON_TYPE)
                .data(item)
                .reconnectDelay(3000)
                .comment("route update")
                .build()), eventSink::close);
    }
}
