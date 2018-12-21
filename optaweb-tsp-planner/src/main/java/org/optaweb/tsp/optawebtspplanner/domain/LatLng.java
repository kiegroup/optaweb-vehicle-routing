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

package org.optaweb.tsp.optawebtspplanner.domain;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Horizontal geographical coordinates consisting of latitude and longitude.
 */
public class LatLng {

    private final BigDecimal latitude;
    private final BigDecimal longitude;

    public LatLng(BigDecimal latitude, BigDecimal longitude) {
        Objects.requireNonNull(latitude);
        Objects.requireNonNull(longitude);
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static LatLng valueOf(double latitude, double longitude) {
        return new LatLng(BigDecimal.valueOf(latitude), BigDecimal.valueOf(longitude));
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LatLng latLng = (LatLng) o;
        return latitude.compareTo(latLng.latitude) == 0 &&
                longitude.compareTo(latLng.longitude) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }

    @Override
    public String toString() {
        return "[" + latitude.toPlainString() +
                ", " + longitude.toPlainString() +
                ']';
    }
}
