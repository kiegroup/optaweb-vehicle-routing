package org.optaweb.vehiclerouting.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class CoordinatesTest {

    @Test
    void constructor_params_must_not_be_null() {
        assertThatNullPointerException().isThrownBy(() -> new Coordinates(null, BigDecimal.ZERO));
        assertThatNullPointerException().isThrownBy(() -> new Coordinates(BigDecimal.ZERO, null));
    }

    @Test
    void coordinates_should_be_equals_when_numerically_equal() {
        Coordinates coordinates = new Coordinates(BigDecimal.valueOf(987.1234), BigDecimal.valueOf(-0.1111));
        assertThat(coordinates).isEqualTo(coordinates);

        BigDecimal ONE_POINT_ZERO = new BigDecimal("1.0");
        BigDecimal MINUS_ZERO = BigDecimal.ZERO.negate();

        Coordinates coordinates01 = new Coordinates(BigDecimal.ZERO, BigDecimal.ONE);
        assertThat(new Coordinates(MINUS_ZERO, ONE_POINT_ZERO))
                .isEqualTo(coordinates01)
                .hasSameHashCodeAs(coordinates01);

        Coordinates coordinates10 = new Coordinates(BigDecimal.ONE, BigDecimal.ZERO);
        assertThat(new Coordinates(ONE_POINT_ZERO, MINUS_ZERO))
                .isEqualTo(coordinates10)
                .hasSameHashCodeAs(coordinates10);
    }

    @Test
    void should_not_equal() {
        assertThat(new Coordinates(BigDecimal.ONE, BigDecimal.TEN))
                .isNotEqualTo(null)
                .isNotEqualTo(BigDecimal.valueOf(11))
                .isNotEqualTo(new Coordinates(BigDecimal.ONE, BigDecimal.ONE))
                .isNotEqualTo(new Coordinates(BigDecimal.TEN, BigDecimal.TEN));
    }

    @Test
    void valueOf_and_getters() {
        double latitude = Math.E;
        double longitude = Math.PI;
        Coordinates coordinates = Coordinates.of(latitude, longitude);
        assertThat(coordinates.latitude()).isEqualTo(BigDecimal.valueOf(latitude));
        assertThat(coordinates.longitude()).isEqualTo(BigDecimal.valueOf(longitude));
    }

    @Test
    void toString_should_contain_latitude_and_longitude() {
        String pi = "3.14159265358979323846";
        assertThat(new Coordinates(BigDecimal.ONE, new BigDecimal(pi))).hasToString("[1, " + pi + "]");
    }
}
