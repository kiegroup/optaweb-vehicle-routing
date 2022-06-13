package org.optaweb.vehiclerouting.plugin.rest.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import org.optaweb.vehiclerouting.domain.Coordinates;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * {@link Coordinates} representation optimized for network transport.
 */
public class PortableCoordinates {

    /*
     * Five decimal places gives "metric" precision (±55 cm on equator). That's enough for visualising the track.
     * https://wiki.openstreetmap.org/wiki/Node#Structure
     */
    private static final int LATLNG_SCALE = 5;

    @JsonProperty(value = "lat")
    private final BigDecimal latitude;
    @JsonProperty(value = "lng")
    private final BigDecimal longitude;

    public static PortableCoordinates fromCoordinates(Coordinates coordinates) {
        Objects.requireNonNull(coordinates, "coordinates must not be null");
        return new PortableCoordinates(
                coordinates.latitude(),
                coordinates.longitude());
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
