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

package org.optaweb.vehiclerouting.plugin.persistence;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.optaweb.vehiclerouting.domain.Distance;
import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.service.distance.DistanceRepository;

@ApplicationScoped
class DistanceRepositoryImpl implements DistanceRepository {

    private final DistanceCrudRepository distanceRepository;

    @Inject
    DistanceRepositoryImpl(DistanceCrudRepository distanceRepository) {
        this.distanceRepository = distanceRepository;
    }

    @Override
    public void saveDistance(Location from, Location to, Distance distance) {
        DistanceEntity distanceEntity = new DistanceEntity(new DistanceKey(from.id(), to.id()), distance.millis());
        distanceRepository.persist(distanceEntity);
    }

    @Override
    public Optional<Distance> getDistance(Location from, Location to) {
        return distanceRepository.findByIdOptional(new DistanceKey(from.id(), to.id()))
                .map(DistanceEntity::getDistance)
                .map(Distance::ofMillis);
    }

    @Override
    public void deleteDistances(Location location) {
        distanceRepository.deleteByFromIdOrToId(location.id());
    }

    @Override
    public void deleteAll() {
        distanceRepository.deleteAll();
    }
}
