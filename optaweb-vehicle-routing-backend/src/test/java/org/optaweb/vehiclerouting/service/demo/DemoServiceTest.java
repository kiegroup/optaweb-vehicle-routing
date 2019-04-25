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
import org.optaweb.vehiclerouting.service.demo.dataset.DataSet;
import org.optaweb.vehiclerouting.service.demo.dataset.Location;
import org.optaweb.vehiclerouting.service.location.LocationService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
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
    @Mock
    private DataSetReader dataSetReader;
    @InjectMocks
    private DemoService demoService;

    private DataSet dataSet;

    @Before
    public void setUp() {
        when(demoProperties.getSize()).thenReturn(-1);
        when(locationService.createLocation(any())).thenReturn(true);
        dataSet = new DataSet();
        dataSet.setDepot(new Location("Hello", 1.0, 7));
        dataSet.getVisits().add(new Location("X Y", 2.0, 9));
        when(dataSetReader.demoDataSet()).thenReturn(dataSet);
    }

    @Test
    public void demo_size_should_equal_visits_plus_depot() {
        assertThat(demoService.getDemoSize()).isEqualTo(dataSet.getVisits().size() + 1);
    }

    @Test
    public void demo_size_can_be_overridden_with_a_property() {
        int demoSizeProperty = 1321;
        when(demoProperties.getSize()).thenReturn(demoSizeProperty);
        assertThat(demoService.getDemoSize()).isEqualTo(demoSizeProperty);
        when(demoProperties.getSize()).thenReturn(0);
        assertThat(demoService.getDemoSize()).isZero();
    }

    @Test
    public void loadDemo() {
        demoService.loadDemo();
        verify(locationService, times(demoService.getDemoSize())).createLocation(any(LatLng.class));
    }

    @Test
    public void retry_when_adding_location_fails() {
        when(locationService.createLocation(any())).thenReturn(false);
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> demoService.loadDemo())
                .withMessageContaining(dataSet.getDepot().toString());
        verify(locationService, times(DemoService.MAX_TRIES)).createLocation(any(LatLng.class));
    }
}
