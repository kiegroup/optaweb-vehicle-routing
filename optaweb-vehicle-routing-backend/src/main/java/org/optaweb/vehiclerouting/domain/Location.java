package org.optaweb.vehiclerouting.domain;

/**
 * A unique location significant to the user.
 */
public class Location extends LocationData {

    private final long id;

    public Location(long id, Coordinates coordinates) {
        // TODO remove this?
        this(id, coordinates, "");
    }

    public Location(long id, Coordinates coordinates, String description) {
        super(coordinates, description);
        this.id = id;
    }

    /**
     * Location's ID.
     *
     * @return unique ID
     */
    public long id() {
        return id;
    }

    /**
     * Full description of the location including its ID, description and coordinates.
     *
     * @return full description
     */
    public String fullDescription() {
        return "[" + id + "]: " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Location location = (Location) o;
        return id == location.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public String toString() {
        return description().isEmpty() ? Long.toString(id) : (id + ": '" + description() + "'");
    }
}
