package org.optaweb.vehiclerouting.domain;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Horizontal geographical coordinates consisting of latitude and longitude.
 */
public class Coordinates {

    private final BigDecimal latitude;
    private final BigDecimal longitude;

    public Coordinates(BigDecimal latitude, BigDecimal longitude) {
        this.latitude = Objects.requireNonNull(latitude);
        this.longitude = Objects.requireNonNull(longitude);
    }

    /**
     * Create coordinates with the given latitude and longitude.
     *
     * @param latitude latitude
     * @param longitude longitude
     * @return coordinates with the given latitude and longitude
     */
    public static Coordinates of(double latitude, double longitude) {
        return new Coordinates(BigDecimal.valueOf(latitude), BigDecimal.valueOf(longitude));
    }

    /**
     * Latitude.
     *
     * @return latitude (never {@code null})
     */
    public BigDecimal latitude() {
        return latitude;
    }

    /**
     * Longitude.
     *
     * @return longitude (never {@code null})
     */
    public BigDecimal longitude() {
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
        Coordinates coordinates = (Coordinates) o;
        return latitude.compareTo(coordinates.latitude) == 0 &&
                longitude.compareTo(coordinates.longitude) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude.doubleValue(), longitude.doubleValue());
    }

    @Override
    public String toString() {
        return "[" + latitude.toPlainString() +
                ", " + longitude.toPlainString() +
                ']';
    }
}
