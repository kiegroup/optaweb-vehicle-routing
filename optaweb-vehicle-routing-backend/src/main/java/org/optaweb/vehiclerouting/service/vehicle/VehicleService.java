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

import java.util.Optional;

import org.optaweb.vehiclerouting.domain.Vehicle;
import org.optaweb.vehiclerouting.domain.VehicleFactory;
import org.optaweb.vehiclerouting.service.location.RouteOptimizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VehicleService {

    static final int DEFAULT_VEHICLE_CAPACITY = 10;

    private final RouteOptimizer optimizer;
    private final VehicleRepository vehicleRepository;

    @Autowired
    public VehicleService(RouteOptimizer optimizer, VehicleRepository vehicleRepository) {
        this.optimizer = optimizer;
        this.vehicleRepository = vehicleRepository;
    }

    public void addVehicle() {
        long id = vehicleRepository.nextId();
        Vehicle vehicle = vehicleRepository.createVehicle("Vehicle " + id, DEFAULT_VEHICLE_CAPACITY);
        optimizer.addVehicle(vehicle);
    }

    public void removeVehicle(long vehicleId) {
        Vehicle vehicle = vehicleRepository.removeVehicle(vehicleId);
        optimizer.removeVehicle(vehicle);
    }

    public synchronized void removeAnyVehicle() {
        Optional<Vehicle> first = vehicleRepository.vehicles().stream().min((o1, o2) -> (int) (o1.id() - o2.id()));
        first.ifPresent(vehicle -> {
            Vehicle removed = vehicleRepository.removeVehicle(vehicle.id());
            optimizer.removeVehicle(removed);
        });
    }

    public void removeAll() {
        optimizer.removeAllVehicles();
        vehicleRepository.removeAll();
    }

    public void changeCapacity(Long vehicleId, int capacity) {
        Vehicle vehicle = vehicleRepository.find(vehicleId).orElseThrow(() -> new IllegalArgumentException(
                "Can't remove Vehicle{id=" + vehicleId + "} because it doesn't exist"
        ));
        Vehicle updatedVehicle = VehicleFactory.createVehicle(vehicle.id(), vehicle.name(), capacity);
        vehicleRepository.update(updatedVehicle);
        optimizer.changeCapacity(updatedVehicle);
    }
}
