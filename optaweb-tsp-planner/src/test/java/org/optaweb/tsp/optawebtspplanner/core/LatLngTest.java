package org.optaweb.tsp.optawebtspplanner.core;

import java.math.BigDecimal;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LatLngTest {

    @Test
    public void constructor_params_must_not_be_null() {
        assertThatThrownBy(() -> new LatLng(null, BigDecimal.ZERO)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new LatLng(BigDecimal.ZERO, null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void latngs_should_be_equals_when_numerically_equal() {
        BigDecimal ONE_POINT_ZERO = new BigDecimal("1.0");
        BigDecimal MINUS_ZERO = BigDecimal.ZERO.negate();
        assertThat(new LatLng(MINUS_ZERO, ONE_POINT_ZERO))
                .isEqualTo(new LatLng(BigDecimal.ZERO, BigDecimal.ONE));
    }
}
