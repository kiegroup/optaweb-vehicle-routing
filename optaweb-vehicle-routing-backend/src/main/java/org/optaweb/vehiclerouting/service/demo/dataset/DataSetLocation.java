package org.optaweb.vehiclerouting.service.demo.dataset;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data set location.
 */
class DataSetLocation {

    private String label;
    @JsonProperty(value = "lat")
    private double latitude;
    @JsonProperty(value = "lng")
    private double longitude;

    private DataSetLocation() {
        // for unmarshalling
    }

    DataSetLocation(String label, double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.label = label;
    }

    /**
     * Location label.
     *
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
     *
     * @return latitude
     */
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Longitude.
     *
     * @return longitude
     */
    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "DataSetLocation{" +
                "label='" + label + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
