package org.optaweb.vehiclerouting.plugin.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class LocationEntityTest {

    @Test
    void constructor_params_must_not_be_null() {
        assertThatNullPointerException().isThrownBy(() -> new LocationEntity(0, null, BigDecimal.ZERO, ""));
        assertThatNullPointerException().isThrownBy(() -> new LocationEntity(0, BigDecimal.ZERO, null, ""));
        assertThatNullPointerException().isThrownBy(() -> new LocationEntity(0, BigDecimal.ZERO, BigDecimal.ONE, null));
    }

    @Test
    void getters() {
        int id = 10;
        BigDecimal latitude = BigDecimal.valueOf(0.101);
        BigDecimal longitude = BigDecimal.valueOf(101.0);
        String description = "Description.";
        LocationEntity locationEntity = new LocationEntity(id, latitude, longitude, description);
        assertThat(locationEntity.getId()).isEqualTo(id);
        assertThat(locationEntity.getLongitude()).isEqualTo(longitude);
        assertThat(locationEntity.getLatitude()).isEqualTo(latitude);
        assertThat(locationEntity.getDescription()).isEqualTo(description);
    }
}
