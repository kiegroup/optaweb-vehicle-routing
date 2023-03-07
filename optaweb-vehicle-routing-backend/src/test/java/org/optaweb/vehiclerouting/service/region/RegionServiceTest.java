package org.optaweb.vehiclerouting.service.region;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.optaweb.vehiclerouting.domain.Coordinates;

@ExtendWith(MockitoExtension.class)
class RegionServiceTest {

    @Mock
    private RegionProperties regionProperties;
    @Mock
    private Region region;
    @InjectMocks
    private RegionService regionService;

    @Test
    void should_return_country_codes_from_properties() {
        List<String> countryCodes = Arrays.asList("XY", "WZ");
        when(regionProperties.countryCodes()).thenReturn(Optional.of(countryCodes));

        assertThat(regionService.countryCodes()).isEqualTo(countryCodes);
    }

    @Test
    void should_return_graphHopper_bounds() {
        BoundingBox boundingBox = new BoundingBox(Coordinates.of(-1, -2), Coordinates.of(3, 4));
        when(region.getBounds()).thenReturn(boundingBox);

        assertThat(regionService.boundingBox()).isEqualTo(boundingBox);
    }
}
