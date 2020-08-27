/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaweb.vehiclerouting.plugin.planner.domain;

import org.optaweb.vehiclerouting.domain.Location;

/**
 * Creates {@link PlanningLocation}s.
 */
public class PlanningLocationFactory {

    private PlanningLocationFactory() {
        throw new AssertionError("Utility class");
    }

    /**
     * Create planning location without a distance map. This location cannot be used for planning but can be used for
     * a problem fact change to remove a visit.
     *
     * @param location domain location
     * @return planning location without distance map
     */
    public static PlanningLocation fromDomain(Location location) {
        return fromDomain(location, PlanningLocationFactory::failFast);
    }

    /**
     * Create planning location from a domain location and a distance map.
     *
     * @param location domain location
     * @param distanceMap distance map of this planning location
     * @return planning location
     */
    public static PlanningLocation fromDomain(Location location, DistanceMap distanceMap) {
        return new PlanningLocation(
                location.id(),
                location.coordinates().latitude().doubleValue(),
                location.coordinates().longitude().doubleValue(),
                distanceMap);
    }

    /**
     * Create test location without distance map and coordinates. Coordinates will be initialized to zero.
     *
     * @param id location ID
     * @return planning location without distance map and coordinates
     */
    public static PlanningLocation testLocation(long id) {
        return testLocation(id, PlanningLocationFactory::failFast);
    }

    /**
     * Create test location with distance map and without coordinates. Coordinates will be initialized to zero.
     *
     * @param id location ID
     * @param distanceMap distance map
     * @return planning location with distance map and without coordinates
     */
    public static PlanningLocation testLocation(long id, DistanceMap distanceMap) {
        return new PlanningLocation(id, 0, 0, distanceMap);
    }

    private static long failFast(PlanningLocation location) {
        throw new IllegalStateException();
    }
}
