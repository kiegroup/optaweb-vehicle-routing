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

package org.optaweb.vehiclerouting.plugin.websocket;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.optaweb.vehiclerouting.domain.Coordinates;

/**
 * {@link Coordinates} representation optimized for network transport.
 */
class PortableCoordinates {

    /*
     * Five decimal places gives "metric" precision (±55 cm on equator). That's enough for visualising the track.
     * https://wiki.openstreetmap.org/wiki/Node#Structure
     */
    private static final int LATLNG_SCALE = 5;

    @JsonProperty(value = "lat")
    private final BigDecimal latitude;
    @JsonProperty(value = "lng")
    private final BigDecimal longitude;

    static PortableCoordinates fromCoordinates(Coordinates coordinates) {
        Objects.requireNonNull(coordinates, "coordinates must not be null");
        return new PortableCoordinates(
                coordinates.latitude(),
                coordinates.longitude()
        );
    }

    private static BigDecimal scale(BigDecimal number) {
        return number.setScale(Math.min(number.scale(), LATLNG_SCALE), RoundingMode.HALF_EVEN).stripTrailingZeros();
    }

    PortableCoordinates(BigDecimal latitude, BigDecimal longitude) {
        this.latitude = scale(latitude);
        this.longitude = scale(longitude);
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
        PortableCoordinates that = (PortableCoordinates) o;
        return Objects.equals(latitude, that.latitude) &&
                Objects.equals(longitude, that.longitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }

    @Override
    public String toString() {
        return "PortableCoordinates{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
