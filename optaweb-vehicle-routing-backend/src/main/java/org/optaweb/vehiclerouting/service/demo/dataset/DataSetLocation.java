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

package org.optaweb.vehiclerouting.service.demo.dataset;

/**
 * Data set location.
 */
class DataSetLocation {

    private String label;
    private double lat;
    private double lng;

    private DataSetLocation() {
        // for unmarshalling
    }

    DataSetLocation(String label, double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
        this.label = label;
    }

    /**
     * Location label.
     * @return label
     */
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Latitude.
     * @return latitude
     */
    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    /**
     * Longitude.
     * @return longitude
     */
    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    @Override
    public String toString() {
        return "DataSetLocation{" +
                "label='" + label + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                '}';
    }
}
