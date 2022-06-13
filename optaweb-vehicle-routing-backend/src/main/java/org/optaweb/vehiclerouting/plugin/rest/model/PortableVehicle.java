package org.optaweb.vehiclerouting.plugin.rest.model;

import java.util.Objects;

import org.optaweb.vehiclerouting.domain.Vehicle;

/**
 * {@link Vehicle} representation suitable for network transport.
 */
public class PortableVehicle {

    private final long id;
    private final String name;
    private final int capacity;

    static PortableVehicle fromVehicle(Vehicle vehicle) {
        Objects.requireNonNull(vehicle, "vehicle must not be null");
        return new PortableVehicle(vehicle.id(), vehicle.name(), vehicle.capacity());
    }

    PortableVehicle(long id, String name, int capacity) {
        this.id = id;
        this.name = Objects.requireNonNull(name);
        this.capacity = capacity;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
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
        PortableVehicle vehicle = (PortableVehicle) o;
        return id == vehicle.id &&
                capacity == vehicle.capacity &&
                name.equals(vehicle.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, capacity);
    }

    @Override
    public String toString() {
        return "PortableVehicle{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", capacity=" + capacity +
                '}';
    }
}
