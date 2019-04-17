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

package org.optaweb.vehiclerouting.service.region;

import java.util.Objects;

import org.optaweb.vehiclerouting.domain.LatLng;

/**
 * Bounding box.
 */
public class BoundingBox {

    private final LatLng southWest;
    private final LatLng northEast;

    /**
     * Create bounding box. The box must have non-zero dimensions and the corners must be south-west and north-east.
     * @param southWest south-west corner (minimal latitude and longitude)
     * @param northEast north-east corner (maximal latitude and longitude)
     */
    public BoundingBox(LatLng southWest, LatLng northEast) {
        this.southWest = Objects.requireNonNull(southWest);
        this.northEast = Objects.requireNonNull(northEast);
        if (southWest.getLatitude().compareTo(northEast.getLatitude()) >= 0) {
            throw new IllegalArgumentException(
                    "South-west corner latitude ("
                            + southWest.getLatitude()
                            + "N) must be less than north-east corner latitude ("
                            + northEast.getLatitude()
                            + "N)");
        }
        if (southWest.getLongitude().compareTo(northEast.getLongitude()) >= 0) {
            throw new IllegalArgumentException(
                    "South-west corner longitude ("
                            + southWest.getLongitude()
                            + "E) must be less than north-east corner longitude ("
                            + northEast.getLongitude()
                            + "E)");
        }
    }

    /**
     * South-west corner of the bounding box.
     * @return south-west corner (minimal latitude and longitude)
     */
    public LatLng getSouthWest() {
        return southWest;
    }

    /**
     * North-east corner of the bounding box.
     * @return north-east corner (maximal latitude and longitude)
     */
    public LatLng getNorthEast() {
        return northEast;
    }
}
