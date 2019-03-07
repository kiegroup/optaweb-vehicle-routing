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
import java.util.Arrays;
import java.util.List;

/**
 * Vehicle's itinerary (sequence of visits).
 */
public class Route {

    private final List<Location> visits;

    public Route(Location... visits) {
        this.visits = Arrays.asList(visits);
    }

    public Route(List<Location> visits) {
        this.visits = new ArrayList<>(visits);
    }

    public List<Location> visits() {
        return visits;
    }
}
