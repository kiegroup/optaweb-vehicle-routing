package org.optaweb.vehiclerouting.plugin.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import org.junit.jupiter.api.Test;

class DistanceEntityTest {

    @Test
    void constructor_params_must_not_be_null() {
        DistanceKey dKey = new DistanceKey(1, 2);
        assertThatNullPointerException().isThrownBy(() -> new DistanceEntity(null, 100L));
        assertThatNullPointerException().isThrownBy(() -> new DistanceEntity(dKey, null));
    }

    @Test
    void equals() {
        final long from = 10;
        final long to = 2000;
        final DistanceKey distanceKey = new DistanceKey(from, to);
        final long distance = 50001;

        DistanceEntity distanceEntity = new DistanceEntity(distanceKey, distance);
        assertThat(distanceEntity)
                .isEqualTo(distanceEntity)
                .isEqualTo(new DistanceEntity(new DistanceKey(from, to), distance))
                .isNotEqualTo(null)
                .isNotEqualTo(distanceKey)
                .isNotEqualTo(new DistanceEntity(distanceKey, distance + 1))
                .isNotEqualTo(new DistanceEntity(new DistanceKey(to, from), distance));
    }
}
