package org.optaweb.vehiclerouting.domain;

import java.util.Objects;

/**
 * Data about a vehicle.
 */
public class VehicleData {

    private final String name;
    private final int capacity;

    VehicleData(String name, int capacity) {
        this.name = Objects.requireNonNull(name);
        this.capacity = capacity;
    }

    /**
     * Vehicle's name (unique description).
     *
     * @return vehicle's name
     */
    public String name() {
        return name;
    }

    /**
     * Vehicle's capacity.
     *
     * @return vehicle's capacity
     */
    public int capacity() {
        return capacity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        VehicleData that = (VehicleData) o;
        return capacity == that.capacity &&
                name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, capacity);
    }

    @Override
    public String toString() {
        return name.isEmpty() ? "<noname>" : "'" + name + "'";
    }
}
