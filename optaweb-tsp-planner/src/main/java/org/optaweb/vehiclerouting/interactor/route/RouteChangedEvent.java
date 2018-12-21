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

package org.optaweb.vehiclerouting.interactor.route;

import java.util.List;

import org.optaweb.vehiclerouting.domain.Location;
import org.springframework.context.ApplicationEvent;

/**
 * Event published when the best route has been changed either by discovering a better route or changing
 * the set of locations.
 */
public class RouteChangedEvent extends ApplicationEvent {

    private final String distance;
    private final List<Location> route;

    /**
     * Create a new ApplicationEvent.
     * @param source the object on which the event initially occurred (never {@code null})
     * @param distance route distance
     * @param route list of locations
     */
    public RouteChangedEvent(Object source, String distance, List<Location> route) {
        super(source);
        this.route = route;
        this.distance = distance;
    }

    public List<Location> getRoute() {
        return route;
    }

    public String getDistance() {
        return distance;
    }
}
