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

package org.optaweb.vehiclerouting.plugin.websocket;

import java.util.Objects;

import org.optaweb.vehiclerouting.domain.Vehicle;

/**
 * {@link Vehicle} representation suitable for network transport.
 */
class PortableVehicle {

    private final long id;
    private final String name;
    private final int capacity;

    static PortableVehicle fromVehicle(Vehicle vehicle) {
        Objects.requireNonNull(vehicle, "vehicle must not be null");
        return new PortableVehicle(vehicle.id(), vehicle.name(), vehicle.capacity());
    }

    PortableVehicle(long id, String name, int capacity) {
        this.id = id;
        this.name = Objects.requireNonNull(name);
        this.capacity = capacity;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
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
        PortableVehicle vehicle = (PortableVehicle) o;
        return id == vehicle.id &&
                capacity == vehicle.capacity &&
                name.equals(vehicle.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, capacity);
    }

    @Override
    public String toString() {
        return "PortableVehicle{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", capacity=" + capacity +
                '}';
    }
}
