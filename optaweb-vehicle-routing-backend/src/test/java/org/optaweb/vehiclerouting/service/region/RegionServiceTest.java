/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaweb.vehiclerouting.service.region;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        when(regionProperties.getCountryCodes()).thenReturn(countryCodes);

        assertThat(regionService.countryCodes()).isEqualTo(countryCodes);
    }

    @Test
    void should_return_graphHopper_bounds() {
        regionService.boundingBox();
        verify(region).getBounds();
    }
}
