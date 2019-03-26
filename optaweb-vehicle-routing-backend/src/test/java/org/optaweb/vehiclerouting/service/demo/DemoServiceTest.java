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

package org.optaweb.vehiclerouting.service.demo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.optaweb.vehiclerouting.domain.LatLng;
import org.optaweb.vehiclerouting.service.location.LocationService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DemoServiceTest {

    @Mock
    private DemoProperties demoProperties;
    @Mock
    private LocationService locationService;
    @InjectMocks
    private DemoService demoService;

    private final int demoSize = 7;

    @Before
    public void setUp() {
        when(demoProperties.getSize()).thenReturn(demoSize);
        when(locationService.createLocation(any())).thenReturn(true);
    }

    @Test
    public void demoSize() {
        assertThat(demoService.getDemoSize()).isEqualTo(demoSize);
    }

    @Test
    public void loadDemo() {
        demoService.loadDemo();
        verify(locationService, times(demoService.getDemoSize())).createLocation(any(LatLng.class));
    }

    @Test
    public void retry_when_adding_location_fails() {
        when(demoProperties.getSize()).thenReturn(1);
        when(locationService.createLocation(any())).thenReturn(false);
        assertThatThrownBy(() -> demoService.loadDemo())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining(Belgium.values()[0].toString());
        verify(locationService, times(DemoService.MAX_TRIES)).createLocation(any(LatLng.class));
    }
}
