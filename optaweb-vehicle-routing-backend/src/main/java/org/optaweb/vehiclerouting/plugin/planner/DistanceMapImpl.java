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

package org.optaweb.vehiclerouting.plugin.planner;

import java.util.Map;

import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.plugin.planner.domain.DistanceMap;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningLocation;

/**
 * Temporary distance map implementation that allows to compute and store distances purely from
 * {@link org.optaweb.vehiclerouting.domain domain objects} and later be queried using Planning domain objects.
 */
// TODO get rid of dependency on Planning domain
public class DistanceMapImpl implements DistanceMap {

    // TODO maybe replace this with Long id (only require the necessary information)
    private final Location location;
    private final Map<Long, Long> distanceMap;

    public DistanceMapImpl(Location location, Map<Long, Long> distanceMap) {
        this.location = location;
        this.distanceMap = distanceMap;
    }

    private boolean containsKey(Object key) {
        return distanceMap.containsKey(((PlanningLocation) key).getId());
    }

    private Long get(Object key) {
        if (!containsKey(key)) {
            throw new IllegalArgumentException(
                    "Distance from " + location
                            + " to " + key
                            + " hasn't been recorded.\n"
                            + "We only know distances to " + distanceMap.keySet());
        }
        return distanceMap.get(((PlanningLocation) key).getId());
    }

    @Override
    public long distanceTo(PlanningLocation location) {
        return get(location);
    }
}
