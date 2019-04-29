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

package org.optaweb.vehiclerouting.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Definition of the vehicle routing problem instance.
 */
public class RoutingProblem {

    private final String name;
    private final Location depot; // FIXME should be Location without ID!
    private final List<Location> visits;

    /**
     * Create routing problem instance.
     * @param name the instance name
     * @param depot the depot (must not be {@code null})
     * @param visits the visits (must not be {@code null})
     */
    public RoutingProblem(String name, Location depot, List<Location> visits) {
        this.name = Objects.requireNonNull(name);
        this.depot = Objects.requireNonNull(depot);
        this.visits = new ArrayList<>(Objects.requireNonNull(visits));
    }

    public String getName() {
        return name;
    }

    public Location getDepot() {
        return depot;
    }

    public List<Location> getVisits() {
        return visits;
    }
}
