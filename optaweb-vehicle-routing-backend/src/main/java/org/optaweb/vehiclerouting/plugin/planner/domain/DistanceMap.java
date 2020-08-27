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

/**
 * Contains travel distances from a reference location to other locations.
 */
@FunctionalInterface
public interface DistanceMap {

    /**
     * Get distance from a reference location to the given location. The actual physical quantity (distance or time)
     * and its units depend on the configuration of the routing engine and is not important for optimization.
     *
     * @param location location the distance of which will be returned
     * @return location's distance
     */
    long distanceTo(PlanningLocation location);
}
