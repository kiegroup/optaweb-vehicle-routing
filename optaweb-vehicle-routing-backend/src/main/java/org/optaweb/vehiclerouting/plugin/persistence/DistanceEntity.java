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

import java.util.Objects;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

/**
 * Distance between two locations that can be persisted.
 */
@Entity
class DistanceEntity {

    @EmbeddedId
    private DistanceKey key;

    private Double distance;

    private DistanceEntity() {
        // for JPA
    }

    DistanceEntity(DistanceKey key, Double distance) {
        this.key = Objects.requireNonNull(key);
        this.distance = Objects.requireNonNull(distance);
    }

    DistanceKey getKey() {
        return key;
    }

    Double getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DistanceEntity that = (DistanceEntity) o;
        return key.equals(that.key) &&
                distance.equals(that.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, distance);
    }
}
