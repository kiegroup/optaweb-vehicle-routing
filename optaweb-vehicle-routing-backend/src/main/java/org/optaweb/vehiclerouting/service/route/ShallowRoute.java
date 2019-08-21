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

package org.optaweb.vehiclerouting.service.route;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO maybe remove this once we fork planning domain from optaplanner-examples
// because then we can hold a reference to the original location

/**
 * Lightweight route description consisting of vehicle and location IDs instead of entities.
 * This makes it easier to quickly construct and share result of route optimization
 * without converting planning domain objects to business domain objects.
 * Specifically, some information may be lost when converting business domain objects to planning domain
 * because it's not needed for optimization (e.g. location address)
 * and so it's impossible to reconstruct the original business object without looking into the repository.
 */
public class ShallowRoute {

    /**
     * Vehicle ID (never {@code null}).
     */
    public final Long vehicleId;
    /**
     * Depot ID (never {@code null}).
     */
    public final Long depotId;
    /**
     * Visit IDs (immutable).
     */
    public final List<Long> visitIds;

    /**
     * Create shallow route.
     * @param vehicleId vehicle ID (never {@code null})
     * @param depotId depot ID (never {@code null})
     * @param visitIds visit IDs
     */
    public ShallowRoute(Long vehicleId, Long depotId, List<Long> visitIds) {
        this.vehicleId = Objects.requireNonNull(vehicleId);
        this.depotId = Objects.requireNonNull(depotId);
        this.visitIds = Collections.unmodifiableList(new ArrayList<>(Objects.requireNonNull(visitIds)));
    }

    @Override
    public String toString() {
        String route = Stream.concat(Stream.of(depotId), visitIds.stream())
                .map(Object::toString)
                .collect(Collectors.joining("->", "[", "]"));
        return vehicleId + ": " + route;
    }
}
