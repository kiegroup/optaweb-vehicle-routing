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

/**
 * Creates {@link Vehicle} instances.
 */
public class VehicleFactory {

    private VehicleFactory() {
        throw new AssertionError("Utility class");
    }

    /**
     * Create a new vehicle with give id, name and capacity.
     * @param id vehicle's ID
     * @param name vehicle's name
     * @param capacity vehicle's capacity
     * @return new vehicle
     */
    public static Vehicle createVehicle(long id, String name, int capacity) {
        return new Vehicle(id, name, capacity);
    }

    /**
     * Create a vehicle with given ID and capacity of zero. The vehicle will have a non-empty name.
     * @param id vehicle's ID
     * @return new testing vehicle instance
     */
    public static Vehicle testVehicle(long id) {
        return new Vehicle(id, "Vehicle " + id, 0);
    }
}
