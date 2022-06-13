package org.optaweb.vehiclerouting.domain;

/**
 * Travel cost (distance between two {@link Location locations} or the length of a {@link Route route}).
 */
public class Distance {

    /**
     * Zero distance, for example the distance from a location to itself.
     */
    public static final Distance ZERO = Distance.ofMillis(0);

    private final long millis;

    /**
     * Create a distance of the given milliseconds.
     *
     * @param millis must be positive or zero
     * @return distance
     */
    public static Distance ofMillis(long millis) {
        return new Distance(millis);
    }

    private Distance(long millis) {
        if (millis < 0) {
            throw new IllegalArgumentException("Milliseconds (" + millis + ") must not be negative.");
        }
        this.millis = millis;
    }

    /**
     * Distance in milliseconds.
     *
     * @return positive number or zero
     */
    public long millis() {
        return millis;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Distance distance = (Distance) o;
        return millis == distance.millis;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(millis);
    }

    @Override
    public String toString() {
        return String.format(
                "%dh %dm %ds %dms",
                millis / 3600_000,
                millis / 60_000 % 60,
                millis / 1000 % 60,
                millis % 1000);
    }
}
