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

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningLocation;

/**
 * Temporary distance map implementation that allows to compute and store distances purely from
 * {@link org.optaweb.vehiclerouting.domain domain objects} and later be queried using Planning domain objects.
 */
// TODO get rid of dependency on Planning domain
class DistanceMap implements Map<PlanningLocation, Double> {

    private final PlanningLocation location;
    private final Map<Long, Double> distanceMap;

    DistanceMap(PlanningLocation location, Map<Long, Double> distanceMap) {
        this.location = location;
        this.distanceMap = distanceMap;
    }

    @Override
    public int size() {
        return distanceMap.size();
    }

    @Override
    public boolean isEmpty() {
        return distanceMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return distanceMap.containsKey(((PlanningLocation) key).getId());
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Double get(Object key) {
        if (!containsKey(key)) {
            throw new IllegalArgumentException(
                    "Distance from " + location
                            + " to " + key
                            + " hasn't been recorded.\n"
                            + "We only know distances to " + distanceMap.keySet());
        }
        // convert millis to secs (required by optaplanner-examples' vehicle routing solution)
        return distanceMap.get(((PlanningLocation) key).getId()) / 1000;
    }

    @Override
    public Double put(PlanningLocation key, Double value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Double remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends PlanningLocation, ? extends Double> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<PlanningLocation> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<Double> values() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Entry<PlanningLocation, Double>> entrySet() {
        throw new UnsupportedOperationException();
    }
}
