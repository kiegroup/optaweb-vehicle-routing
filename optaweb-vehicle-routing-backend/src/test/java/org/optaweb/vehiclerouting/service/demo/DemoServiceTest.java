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

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.optaweb.vehiclerouting.domain.LatLng;
import org.optaweb.vehiclerouting.service.location.LocationService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DemoServiceTest {

    @Mock
    private LocationService locationService;
    @InjectMocks
    private DemoService demoService;

    @Test
    public void demoSize() {
        Assertions.assertThat(demoService.getDemoSize()).isEqualTo(Belgium.values().length);
    }

    @Test
    public void loadDemo() {
        demoService.loadDemo();
        verify(locationService, times(demoService.getDemoSize())).createLocation(any(LatLng.class));
    }
}
