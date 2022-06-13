package org.optaweb.vehiclerouting.plugin.persistence;

import java.util.Objects;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

/**
 * Distance between two locations that can be persisted.
 */
@Entity
class DistanceEntity {

    @EmbeddedId
    private DistanceKey key;

    private Long distance;

    protected DistanceEntity() {
        // for JPA
    }

    DistanceEntity(DistanceKey key, Long distance) {
        this.key = Objects.requireNonNull(key);
        this.distance = Objects.requireNonNull(distance);
    }

    DistanceKey getKey() {
        return key;
    }

    Long getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DistanceEntity that = (DistanceEntity) o;
        return key.equals(that.key) &&
                distance.equals(that.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, distance);
    }
}
