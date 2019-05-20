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

import com.graphhopper.reader.osm.GraphHopperOSM;
import com.graphhopper.storage.GraphHopperStorage;
import com.graphhopper.util.shapes.BBox;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.optaweb.vehiclerouting.domain.Coordinates;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RegionServiceTest {

    @Mock
    private RegionProperties regionProperties;
    @Mock
    private GraphHopperOSM graphHopper;
    @InjectMocks
    private RegionService regionService;
    @Mock
    private GraphHopperStorage graphHopperStorage;

    @Test
    public void should_return_country_codes_from_properties() {
        List<String> countryCodes = Arrays.asList("XY", "WZ");
        when(regionProperties.getCountryCodes()).thenReturn(countryCodes);

        assertThat(regionService.countryCodes()).isEqualTo(countryCodes);
    }

    @Test
    public void should_return_graphHopper_bounds() {
        when(graphHopper.getGraphHopperStorage()).thenReturn(graphHopperStorage);
        double minLat_Y = -90;
        double minLon_X = -180;
        double maxLat_Y = 90;
        double maxLon_X = 180;
        BBox bbox = new BBox(minLon_X, maxLon_X, minLat_Y, maxLat_Y);
        when(graphHopperStorage.getBounds()).thenReturn(bbox);

        BoundingBox boundingBox = regionService.boundingBox();

        assertThat(boundingBox.getSouthWest()).isEqualTo(Coordinates.valueOf(minLat_Y, minLon_X));
        assertThat(boundingBox.getNorthEast()).isEqualTo(Coordinates.valueOf(maxLat_Y, maxLon_X));
    }
}
