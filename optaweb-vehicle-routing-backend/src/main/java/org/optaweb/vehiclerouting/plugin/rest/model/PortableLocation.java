package org.optaweb.vehiclerouting.plugin.rest.model;

import java.math.BigDecimal;
import java.util.Objects;

import org.optaweb.vehiclerouting.domain.Location;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * {@link Location} representation convenient for marshalling.
 */
public class PortableLocation {

    private final long id;

    @JsonProperty(value = "lat", required = true)
    private final BigDecimal latitude;
    @JsonProperty(value = "lng", required = true)
    private final BigDecimal longitude;

    private final String description;

    static PortableLocation fromLocation(Location location) {
        Objects.requireNonNull(location, "location must not be null");
        return new PortableLocation(
                location.id(),
                location.coordinates().latitude(),
                location.coordinates().longitude(),
                location.description());
    }

    @JsonCreator
    public PortableLocation(
            @JsonProperty(value = "id") long id,
            @JsonProperty(value = "lat") BigDecimal latitude,
            @JsonProperty(value = "lng") BigDecimal longitude,
            @JsonProperty(value = "description") String description) {
        this.id = id;
        this.latitude = Objects.requireNonNull(latitude);
        this.longitude = Objects.requireNonNull(longitude);
        this.description = Objects.requireNonNull(description);
    }

    public long getId() {
        return id;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PortableLocation that = (PortableLocation) o;
        return id == that.id &&
                description.equals(that.description) &&
                latitude.compareTo(that.latitude) == 0 &&
                longitude.compareTo(that.longitude) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, latitude, longitude);
    }

    @Override
    public String toString() {
        return "PortableLocation{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
