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
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Vehicle's itinerary (sequence of visits) and depot.
 */
public class Route {

    private final Location depot;
    private final List<Location> visits;

    public Route(Location depot, List<Location> visits) {
        this.depot = Objects.requireNonNull(depot);
        this.visits = new ArrayList<>(Objects.requireNonNull(visits));
        // TODO Probably remove this check when we have more types: new Route(Depot depot, List<Visit> visits).
        //      Then visits obviously cannot contain the depot. But will we still require that no visit has the same
        //      location as the depot? (I don't think so).
        if (visits.contains(depot)) {
            throw new IllegalArgumentException("Depot (" + depot + ") must not be one of the visits (" + visits + ")");
        }
        long uniqueVisits = visits.stream().distinct().count();
        if (uniqueVisits < visits.size()) {
            long duplicates = visits.size() - uniqueVisits;
            throw new IllegalArgumentException("Some customer have been visited multiple times (" + duplicates + ")");
        }
    }

    public Location depot() {
        return depot;
    }

    public List<Location> visits() {
        return Collections.unmodifiableList(visits);
    }
}
