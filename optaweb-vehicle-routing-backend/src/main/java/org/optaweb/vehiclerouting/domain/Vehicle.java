package org.optaweb.vehiclerouting.domain;

/**
 * Vehicle that can be used to deliver cargo to visits.
 */
public class Vehicle extends VehicleData {

    private final long id;

    Vehicle(long id, String name, int capacity) {
        super(name, capacity);
        this.id = id;
    }

    /**
     * Vehicle's ID.
     *
     * @return unique ID
     */
    public long id() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Vehicle vehicle = (Vehicle) o;
        return id == vehicle.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public String toString() {
        return name().isEmpty() ? Long.toString(id) : (id + ": '" + name() + "'");
    }
}
