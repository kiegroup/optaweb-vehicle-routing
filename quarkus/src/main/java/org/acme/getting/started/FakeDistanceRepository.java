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

import javax.enterprise.context.ApplicationScoped;

import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.service.distance.DistanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.arc.AlternativePriority;

@ApplicationScoped
@AlternativePriority(1)
public class FakeDistanceRepository implements DistanceRepository {

    private static final Logger logger = LoggerFactory.getLogger(FakeDistanceRepository.class);

    @Override
    public void saveDistance(Location from, Location to, long distance) {
        logger.info("SAVE");
    }

    @Override
    public long getDistance(Location from, Location to) {
        logger.info("GET");
        return -1;
    }

    @Override
    public void deleteDistances(Location location) {
        logger.info("DELETE");
    }

    @Override
    public void deleteAll() {
        logger.info("DELETE ALL");
    }
}
