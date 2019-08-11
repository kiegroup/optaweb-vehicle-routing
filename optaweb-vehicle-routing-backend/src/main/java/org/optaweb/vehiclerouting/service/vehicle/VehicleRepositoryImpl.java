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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.optaweb.vehiclerouting.domain.Vehicle;
import org.optaweb.vehiclerouting.domain.VehicleFactory;
import org.springframework.stereotype.Component;

@Component
public class VehicleRepositoryImpl implements VehicleRepository {

    private long idSequence = 0;
    private Map<Long, Vehicle> vehicles = new HashMap<>(10);

    @Override
    public Vehicle createVehicle(String name, int capacity) {
        Vehicle vehicle = VehicleFactory.createVehicle(idSequence++, name, capacity);
        vehicles.put(vehicle.id(), vehicle);
        return vehicle;
    }

    @Override
    public List<Vehicle> vehicles() {
        return Collections.unmodifiableList(
                vehicles.values().stream()
                        .sorted((o1, o2) -> Long.signum(o2.id() - o1.id()))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Vehicle removeVehicle(long id) {
        return vehicles.remove(id);
    }

    @Override
    public void removeAll() {
        vehicles.clear();
    }

    @Override
    public Optional<Vehicle> find(Long vehicleId) {
        return Optional.ofNullable(vehicles.get(vehicleId));
    }

    @Override
    public long nextId() {
        return idSequence;
    }

    @Override
    public void update(Vehicle vehicle) {
        vehicles.put(vehicle.id(), vehicle);
    }
}
