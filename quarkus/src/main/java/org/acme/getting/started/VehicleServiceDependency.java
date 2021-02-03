/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.acme.getting.started;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import org.optaweb.vehiclerouting.domain.Vehicle;
import org.optaweb.vehiclerouting.domain.VehicleData;
import org.optaweb.vehiclerouting.domain.VehicleFactory;
import org.optaweb.vehiclerouting.service.vehicle.VehiclePlanner;
import org.optaweb.vehiclerouting.service.vehicle.VehicleRepository;

@ApplicationScoped
public class VehicleServiceDependency implements VehicleRepository, VehiclePlanner {

    @Override
    public void addVehicle(Vehicle vehicle) {

    }

    @Override
    public void removeVehicle(Vehicle vehicle) {

    }

    @Override
    public void removeAllVehicles() {

    }

    @Override
    public void changeCapacity(Vehicle vehicle) {

    }

    @Override
    public Vehicle createVehicle(int capacity) {
        return VehicleFactory.createVehicle(1, "Vehicle 1", capacity);
    }

    @Override
    public Vehicle createVehicle(VehicleData vehicleData) {
        return null;
    }

    @Override
    public List<Vehicle> vehicles() {
        return null;
    }

    @Override
    public Vehicle removeVehicle(long id) {
        return null;
    }

    @Override
    public void removeAll() {

    }

    @Override
    public Optional<Vehicle> find(long vehicleId) {
        return Optional.of(VehicleFactory.createVehicle(vehicleId, "Fake vehicle", 99));
    }

    @Override
    public void update(Vehicle vehicle) {

    }
}
