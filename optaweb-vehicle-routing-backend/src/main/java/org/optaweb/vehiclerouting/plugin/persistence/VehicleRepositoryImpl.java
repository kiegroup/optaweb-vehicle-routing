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

package org.optaweb.vehiclerouting.plugin.persistence;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.optaweb.vehiclerouting.domain.Vehicle;
import org.optaweb.vehiclerouting.domain.VehicleData;
import org.optaweb.vehiclerouting.domain.VehicleFactory;
import org.optaweb.vehiclerouting.service.vehicle.VehicleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class VehicleRepositoryImpl implements VehicleRepository {

    private static final Logger logger = LoggerFactory.getLogger(VehicleRepositoryImpl.class);
    private final VehicleCrudRepository repository;

    public VehicleRepositoryImpl(VehicleCrudRepository repository) {
        this.repository = repository;
    }

    @Override
    public Vehicle createVehicle(int capacity) {
        long id = repository.save(new VehicleEntity(0, null, capacity)).getId();
        VehicleEntity vehicleEntity = repository.save(new VehicleEntity(id, "Vehicle " + id, capacity));
        return toDomain(vehicleEntity);
    }

    @Override
    public Vehicle createVehicle(VehicleData vehicleData) {
        VehicleEntity vehicleEntity = repository.save(new VehicleEntity(0, vehicleData.name(), vehicleData.capacity()));
        return toDomain(vehicleEntity);
    }

    @Override
    public List<Vehicle> vehicles() {
        return StreamSupport.stream(repository.findAll().spliterator(), false)
                .map(VehicleRepositoryImpl::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Vehicle removeVehicle(long id) {
        Optional<VehicleEntity> optionalVehicleEntity = repository.findById(id);
        VehicleEntity vehicleEntity = optionalVehicleEntity.orElseThrow(
                () -> new IllegalArgumentException("Vehicle{id=" + id + "} doesn't exist")
        );
        repository.deleteById(id);
        Vehicle vehicle = toDomain(vehicleEntity);
        logger.info("Deleted {}", vehicle);
        return vehicle;
    }

    @Override
    public void removeAll() {
        repository.deleteAll();
    }

    @Override
    public Optional<Vehicle> find(long vehicleId) {
        return repository.findById(vehicleId).map(VehicleRepositoryImpl::toDomain);
    }

    @Override
    public void update(Vehicle vehicle) {
        repository.save(new VehicleEntity(vehicle.id(), vehicle.name(), vehicle.capacity()));
    }

    private static Vehicle toDomain(VehicleEntity vehicleEntity) {
        return VehicleFactory.createVehicle(
                vehicleEntity.getId(),
                vehicleEntity.getName(),
                vehicleEntity.getCapacity()
        );
    }
}
