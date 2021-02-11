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

package org.optaweb.vehiclerouting.plugin.rest.model;

import java.util.Objects;

import org.optaweb.vehiclerouting.domain.Distance;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Portable representation of a {@link Distance distance}.
 */
public class PortableDistance {

    @JsonValue
    private final String distance;

    static PortableDistance fromDistance(Distance distance) {
        long seconds = (Objects.requireNonNull(distance).millis() + 500) / 1000;
        return new PortableDistance(String.format("%dh %dm %ds", seconds / 3600, seconds / 60 % 60, seconds % 60));
    }

    private PortableDistance(String distance) {
        this.distance = distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PortableDistance that = (PortableDistance) o;
        return distance.equals(that.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(distance);
    }

    @Override
    public String toString() {
        return "PortableDistance{" +
                "distance='" + distance + '\'' +
                '}';
    }
}
