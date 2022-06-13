package org.optaweb.vehiclerouting.plugin.persistence;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Embeddable;

/**
 * Composite key for {@link DistanceEntity}.
 */
@Embeddable
class DistanceKey implements Serializable {

    // TODO make it a foreign key to LocationEntity
    private Long fromId;
    private Long toId;

    protected DistanceKey() {
        // for JPA
    }

    DistanceKey(long fromId, long toId) {
        this.fromId = fromId;
        this.toId = toId;
    }

    Long getFromId() {
        return fromId;
    }

    void setFromId(Long fromId) {
        this.fromId = fromId;
    }

    Long getToId() {
        return toId;
    }

    void setToId(Long toId) {
        this.toId = toId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DistanceKey that = (DistanceKey) o;
        return fromId.equals(that.fromId) &&
                toId.equals(that.toId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromId, toId);
    }
}
