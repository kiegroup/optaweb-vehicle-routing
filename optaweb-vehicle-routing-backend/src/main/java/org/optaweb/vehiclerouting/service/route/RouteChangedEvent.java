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
import java.util.Optional;

import org.springframework.context.ApplicationEvent;

/**
 * Event published when the routing plan has been updated either by discovering a better route or by changing
 * the set of locations.
 */
public class RouteChangedEvent extends ApplicationEvent {

    private final String distance;
    private final Long depotId;
    private final Collection<ShallowRoute> routes;

    /**
     * Create a new ApplicationEvent.
     * @param source the object on which the event initially occurred (never {@code null})
     * @param distance total distance of all vehicle routes
     * @param depotId depot ID. May be null if there are no locations.
     * @param routes vehicle routes
     */
    public RouteChangedEvent(Object source, String distance, Long depotId, Collection<ShallowRoute> routes) {
        super(source);
        this.distance = Objects.requireNonNull(distance);
        this.depotId = depotId;
        this.routes = Objects.requireNonNull(routes);
    }

    /**
     * Routes of all vehicles.
     * @return vehicle routes
     */
    public Collection<ShallowRoute> routes() {
        return routes;
    }

    public String distance() {
        return distance;
    }

    /**
     * The depot ID.
     * @return depot ID
     */
    public Optional<Long> depot() {
        return Optional.ofNullable(depotId);
    }
}
