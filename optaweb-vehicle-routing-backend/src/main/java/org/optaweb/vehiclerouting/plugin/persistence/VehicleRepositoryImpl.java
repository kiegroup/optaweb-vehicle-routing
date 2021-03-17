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

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.optaweb.vehiclerouting.domain.Vehicle;
import org.optaweb.vehiclerouting.domain.VehicleData;
import org.optaweb.vehiclerouting.domain.VehicleFactory;
import org.optaweb.vehiclerouting.service.vehicle.VehicleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class VehicleRepositoryImpl implements VehicleRepository {

    private static final Logger logger = LoggerFactory.getLogger(VehicleRepositoryImpl.class);
    private final VehicleCrudRepository repository;

    @Inject
    public VehicleRepositoryImpl(VehicleCrudRepository repository) {
        this.repository = repository;
    }

    @Override
    public Vehicle createVehicle(int capacity) {
        VehicleEntity vehicleEntity = new VehicleEntity(0, null, capacity);
        repository.persist(vehicleEntity);
        vehicleEntity.setName("Vehicle " + vehicleEntity.getId());
        Vehicle vehicle = toDomain(vehicleEntity);
        logger.info("Created vehicle {}.", vehicle);
        return vehicle;
    }

    @Override
    public Vehicle createVehicle(VehicleData vehicleData) {
        VehicleEntity vehicleEntity = new VehicleEntity(0, vehicleData.name(), vehicleData.capacity());
        repository.persist(vehicleEntity);
        Vehicle vehicle = toDomain(vehicleEntity);
        logger.info("Created vehicle {}.", vehicle);
        return vehicle;
    }

    @Override
    public List<Vehicle> vehicles() {
        return repository.streamAll()
                .map(VehicleRepositoryImpl::toDomain)
                .collect(toList());
    }

    @Override
    public Vehicle removeVehicle(long id) {
        Optional<VehicleEntity> optionalVehicleEntity = repository.findByIdOptional(id);
        VehicleEntity vehicleEntity = optionalVehicleEntity.orElseThrow(
                () -> new IllegalArgumentException("Vehicle{id=" + id + "} doesn't exist"));
        repository.deleteById(id);
        Vehicle vehicle = toDomain(vehicleEntity);
        logger.info("Deleted vehicle {}.", vehicle);
        return vehicle;
    }

    @Override
    public void removeAll() {
        repository.deleteAll();
    }

    @Override
    public Optional<Vehicle> find(long vehicleId) {
        return repository.findByIdOptional(vehicleId).map(VehicleRepositoryImpl::toDomain);
    }

    @Override
    public Vehicle changeCapacity(long vehicleId, int capacity) {
        VehicleEntity vehicleEntity = repository.findByIdOptional(vehicleId).orElseThrow(() -> new IllegalArgumentException(
                "Can't change Vehicle{id=" + vehicleId + "} because it doesn't exist"));
        vehicleEntity.setCapacity(capacity);
        repository.flush();
        return VehicleFactory.createVehicle(vehicleEntity.getId(), vehicleEntity.getName(), vehicleEntity.getCapacity());
    }

    private static Vehicle toDomain(VehicleEntity vehicleEntity) {
        return VehicleFactory.createVehicle(
                vehicleEntity.getId(),
                vehicleEntity.getName(),
                vehicleEntity.getCapacity());
    }
}
