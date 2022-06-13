package org.optaweb.vehiclerouting.domain;

import java.util.Objects;

/**
 * Location properties. It's not an entity yet (it doesn't have an identity, it's a value object).
 * It might be the data about a location sent from a client or data stored in a file,
 * ready to be loaded but not yet tied to a specific location entity.
 */
public class LocationData {

    private final Coordinates coordinates;
    private final String description;

    /**
     * Create location data.
     *
     * @param coordinates never {@code null}
     * @param description never {@code null}
     */
    public LocationData(Coordinates coordinates, String description) {
        this.coordinates = Objects.requireNonNull(coordinates);
        this.description = Objects.requireNonNull(description);
    }

    /**
     * Location coordinates.
     *
     * @return coordinates (never {@code null})
     */
    public Coordinates coordinates() {
        return coordinates;
    }

    /**
     * Location description.
     *
     * @return description (never {@code null})
     */
    public String description() {
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
        LocationData that = (LocationData) o;
        return coordinates.equals(that.coordinates) &&
                description.equals(that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coordinates, description);
    }

    @Override
    public String toString() {
        return (description.isEmpty() ? "<noname>" : "'" + description + "'") + " " + coordinates;
    }
}
