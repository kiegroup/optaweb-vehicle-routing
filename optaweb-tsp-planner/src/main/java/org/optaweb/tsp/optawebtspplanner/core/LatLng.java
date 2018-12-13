package org.optaweb.tsp.optawebtspplanner.core;

import java.math.BigDecimal;
import java.util.Objects;

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
