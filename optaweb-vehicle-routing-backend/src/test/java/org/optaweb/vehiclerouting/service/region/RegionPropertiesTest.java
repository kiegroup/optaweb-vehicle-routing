package org.optaweb.vehiclerouting.service.region;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class RegionPropertiesTest {

    @Inject
    RegionProperties regionProperties;

    @Test
    void test() {
        assertThat(regionProperties.countryCodes()).contains(List.of("AT", "DE"));
    }
}
