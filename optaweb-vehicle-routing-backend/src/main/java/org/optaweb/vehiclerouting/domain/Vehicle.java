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

import java.util.Objects;

/**
 * Vehicle that can be used to deliver cargo to visits.
 */
public class Vehicle {

    private final long id;
    private final String name;
    private final int capacity;

    Vehicle(long id, String name, int capacity) {
        this.id = id;
        this.name = Objects.requireNonNull(name);
        this.capacity = capacity;
    }

    /**
     * Vehicle's ID.
     * @return unique ID
     */
    public long id() {
        return id;
    }

    /**
     * Vehicle's name (unique description).
     * @return vehicle's name
     */
    public String name() {
        return name;
    }

    /**
     * Vehicle's capacity.
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
        Vehicle vehicle = (Vehicle) o;
        return id == vehicle.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", capacity=" + capacity +
                '}';
    }
}
