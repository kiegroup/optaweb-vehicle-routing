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

import java.util.Objects;

/**
 * Data about a vehicle.
 */
public class VehicleData {

    private final String name;
    private final int capacity;

    VehicleData(String name, int capacity) {
        this.name = Objects.requireNonNull(name);
        this.capacity = capacity;
    }

    /**
     * Vehicle's name (unique description).
     *
     * @return vehicle's name
     */
    public String name() {
        return name;
    }

    /**
     * Vehicle's capacity.
     *
     * @return vehicle's capacity
     */
    public int capacity() {
        return capacity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        VehicleData that = (VehicleData) o;
        return capacity == that.capacity &&
                name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, capacity);
    }

    @Override
    public String toString() {
        return name.isEmpty() ? "<noname>" : "'" + name + "'";
    }
}
