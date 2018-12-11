package org.optaweb.tsp.optawebtspplanner.core;

import java.math.BigDecimal;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LocationTest {

    @Test
    public void constructor_params_must_not_be_null() {
        assertThatThrownBy(() -> new Location(0, null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void locations_are_equal_only_if_they_have_same_id_and_coordinate() {
        LatLng latLng0 = new LatLng(BigDecimal.ZERO, BigDecimal.ZERO);
        LatLng latLng1 = new LatLng(BigDecimal.ONE, BigDecimal.ONE);

        final Location location = new Location(0, latLng0);

        // different ID
        assertThat(location).isNotEqualTo(new Location(1, latLng0));
        // different coordinate
        assertThat(location).isNotEqualTo(new Location(0, latLng1));
        // null
        assertThat(location).isNotEqualTo(null);
        // same properties -> OK
        assertThat(location).isEqualTo(new Location(0, latLng0));
    }
}
