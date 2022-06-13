package org.optaweb.vehiclerouting.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.optaweb.vehiclerouting.domain.CountryCodeValidator.validate;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

class CountryCodeValidatorTest {

    @Test
    void should_fail_on_invalid_country_codes() {
        assertThatNullPointerException().isThrownBy(() -> validate(null));
        assertThatNullPointerException().isThrownBy(() -> validate(Arrays.asList("US", null, "CA")));
        assertThatIllegalArgumentException().isThrownBy(() -> validate(Arrays.asList("XX")));
        assertThatIllegalArgumentException().isThrownBy(() -> validate(Arrays.asList("CZE")));
        assertThatIllegalArgumentException().isThrownBy(() -> validate(Arrays.asList("D")));
        assertThatIllegalArgumentException().isThrownBy(() -> validate(Arrays.asList("")));
        assertThatIllegalArgumentException().isThrownBy(() -> validate(Arrays.asList("US", "XY", "CA")));
    }

    @Test
    void should_ignore_case_and_convert_to_upper_case() {
        assertThat(validate(Arrays.asList("us"))).containsExactly("US");
    }

    @Test
    void should_allow_multiple_values() {
        assertThat(validate(Arrays.asList("US", "ca"))).containsExactly("US", "CA");
    }

    @Test
    void should_allow_empty_list() {
        assertThat(validate(new ArrayList<>())).isEmpty();
    }
}
