package org.optaweb.vehiclerouting.plugin.rest.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import org.junit.jupiter.api.Test;
import org.optaweb.vehiclerouting.domain.Distance;
import org.optaweb.vehiclerouting.util.jackson.JacksonAssertions;

class PortableDistanceTest {

    @Test
    void marshal_to_json() {
        Distance distance = Distance.ofMillis(3_661_987);
        PortableDistance portableDistance = PortableDistance.fromDistance(distance);
        JacksonAssertions.assertThat(portableDistance).serializedIsEqualToJson("\"1h 1m 2s\"");
    }

    @Test
    void from_distance() {
        assertThatNullPointerException().isThrownBy(() -> PortableDistance.fromDistance(null));
    }

    @Test
    void equals_hashCode_toString() {
        long millis = 173_000;
        Distance distance = Distance.ofMillis(millis);
        PortableDistance portableDistance = PortableDistance.fromDistance(distance);

        assertThat(portableDistance)
                // equals()
                .isEqualTo(portableDistance)
                .isEqualTo(PortableDistance.fromDistance(distance))
                .isNotEqualTo(null)
                .isNotEqualTo(millis)
                .isNotEqualTo(PortableDistance.fromDistance(Distance.ofMillis(millis - 501)))
                // hashCode()
                .hasSameHashCodeAs(PortableDistance.fromDistance(distance))
                // toString()
                .asString().contains("0h 2m 53s");
    }
}
