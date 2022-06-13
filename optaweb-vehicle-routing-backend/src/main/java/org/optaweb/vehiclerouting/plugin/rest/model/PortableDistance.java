package org.optaweb.vehiclerouting.plugin.rest.model;

import java.util.Objects;

import org.optaweb.vehiclerouting.domain.Distance;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Portable representation of a {@link Distance distance}.
 */
public class PortableDistance {

    @JsonValue
    private final String distance;

    static PortableDistance fromDistance(Distance distance) {
        long seconds = (Objects.requireNonNull(distance).millis() + 500) / 1000;
        return new PortableDistance(String.format("%dh %dm %ds", seconds / 3600, seconds / 60 % 60, seconds % 60));
    }

    private PortableDistance(String distance) {
        this.distance = distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PortableDistance that = (PortableDistance) o;
        return distance.equals(that.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(distance);
    }

    @Override
    public String toString() {
        return "PortableDistance{" +
                "distance='" + distance + '\'' +
                '}';
    }
}
