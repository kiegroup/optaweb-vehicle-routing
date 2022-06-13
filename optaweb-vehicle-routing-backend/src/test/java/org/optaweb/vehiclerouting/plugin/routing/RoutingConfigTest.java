package org.optaweb.vehiclerouting.plugin.routing;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class RoutingConfigTest {

    @Test
    void should_throw_exception_when_url_is_malformed() {
        Path osmFile = Mockito.mock(Path.class);
        String malformedUrl = "x+y";
        assertThatExceptionOfType(RoutingEngineException.class)
                .isThrownBy(() -> RoutingConfig.downloadOsmFile(malformedUrl, osmFile))
                .withMessageContaining("malformed");
    }
}
