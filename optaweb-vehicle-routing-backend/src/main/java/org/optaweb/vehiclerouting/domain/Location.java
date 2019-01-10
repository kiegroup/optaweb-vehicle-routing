/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import java.util.Objects;

/**
 * A unique location significant to the user.
 */
public class Location {

    private final long id;
    private final LatLng latLng;

    public Location(long id, LatLng latLng) {
        this.id = id;
        this.latLng = Objects.requireNonNull(latLng);
    }

    public long getId() {
        return id;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Location that = (Location) o;
        return id == that.id &&
                latLng.equals(that.latLng);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, latLng);
    }

    @Override
    public String toString() {
        return "Location{" +
                "id=" + id +
                ", latLng=" + latLng +
                '}';
    }
}
