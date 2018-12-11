package org.optaweb.tsp.optawebtspplanner.core;

import java.math.BigDecimal;
import java.util.Objects;

public class LatLng {

    private final BigDecimal latitude;
    private final BigDecimal longitude;

    public LatLng(BigDecimal latitude, BigDecimal longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
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
        return latitude.equals(latLng.latitude) &&
                longitude.equals(latLng.longitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }
}
