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

package org.optaweb.vehiclerouting.service.vehicle;

import java.util.List;
import java.util.Optional;

import org.optaweb.vehiclerouting.domain.Vehicle;

/**
 * Defines repository operations on vehicles.
 */
public interface VehicleRepository {

    /**
     * Create a vehicle with a unique ID.
     * @param name vehicle name
     * @param capacity vehicle's capacity
     * @return a new vehicle
     */
    Vehicle createVehicle(String name, int capacity);

    /**
     * Get all vehicles.
     * @return all vehicles
     */
    List<Vehicle> vehicles();

    /**
     * Remove vehicle.
     * @param id vehicle's id
     * @return the removed vehicle
     */
    Vehicle removeVehicle(long id);

    /**
     * Remove all vehicles from the repository.
     */
    void removeAll();

    Optional<Vehicle> find(Long vehicleId);

    /**
     * Temporary hack needed for vehicle name auto-generation.
     * @return unique ID that will be used for the next new vehicle.
     */
    long nextId();

    void update(Vehicle vehicle);
}
