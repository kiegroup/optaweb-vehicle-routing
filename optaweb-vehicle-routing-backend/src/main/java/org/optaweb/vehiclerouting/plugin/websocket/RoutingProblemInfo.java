/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.optaweb.vehiclerouting.plugin.websocket;

import java.util.Objects;

import org.optaweb.vehiclerouting.domain.RoutingProblem;

/**
 * Information about a {@link RoutingProblem routing problem instance}.
 */
class RoutingProblemInfo {

    private final String name;
    private final int visits;

    RoutingProblemInfo(String name, int visits) {
        this.name = Objects.requireNonNull(name);
        this.visits = visits;
    }

    /**
     * Routing problem instance name.
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Number of visits in the routing problem instance.
     * @return number of visits
     */
    public int getVisits() {
        return visits;
    }
}
