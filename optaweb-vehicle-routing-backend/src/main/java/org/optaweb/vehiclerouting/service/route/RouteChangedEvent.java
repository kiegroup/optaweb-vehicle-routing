/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.optaweb.vehiclerouting.service.route;

import java.util.Collection;
import java.util.Objects;

import org.optaweb.vehiclerouting.domain.Route;
import org.springframework.context.ApplicationEvent;

/**
 * Event published when the best route has been changed either by discovering a better route or changing
 * the set of locations.
 */
public class RouteChangedEvent extends ApplicationEvent {

    private final String distance;
    private final Collection<Route> routes;

    /**
     * Create a new ApplicationEvent.
     * @param source the object on which the event initially occurred (never {@code null})
     * @param distance total distance of all vehicle routes
     * @param routes vehicle routes
     */
    public RouteChangedEvent(Object source, String distance, Collection<Route> routes) {
        super(source);
        this.distance = Objects.requireNonNull(distance);
        this.routes = Objects.requireNonNull(routes);
    }

    public Collection<Route> routes() {
        return routes;
    }

    public String distance() {
        return distance;
    }
}
