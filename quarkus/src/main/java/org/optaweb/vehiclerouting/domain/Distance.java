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

package org.optaweb.vehiclerouting.domain;

/**
 * Travel cost (distance between two {@link Location locations} or the length of a {@link Route route}).
 */
public class Distance {

    /**
     * Zero distance, for example the distance from a location to itself.
     */
    public static final Distance ZERO = Distance.ofMillis(0);

    private final long millis;

    /**
     * Create a distance of the given milliseconds.
     *
     * @param millis must be positive or zero
     * @return distance
     */
    public static Distance ofMillis(long millis) {
        return new Distance(millis);
    }

    private Distance(long millis) {
        if (millis < 0) {
            throw new IllegalArgumentException("Milliseconds (" + millis + ") must not be negative.");
        }
        this.millis = millis;
    }

    /**
     * Distance in milliseconds.
     *
     * @return positive number or zero
     */
    public long millis() {
        return millis;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Distance distance = (Distance) o;
        return millis == distance.millis;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(millis);
    }

    @Override
    public String toString() {
        return String.format(
                "%dh %dm %ds %dms",
                millis / 3600_000,
                millis / 60_000 % 60,
                millis / 1000 % 60,
                millis % 1000);
    }
}
