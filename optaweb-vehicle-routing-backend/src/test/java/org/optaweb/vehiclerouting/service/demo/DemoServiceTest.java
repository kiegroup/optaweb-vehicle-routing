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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.optaweb.vehiclerouting.domain.LatLng;
import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.domain.RoutingProblem;
import org.optaweb.vehiclerouting.service.demo.dataset.DataSetMarshaller;
import org.optaweb.vehiclerouting.service.location.LocationRepository;
import org.optaweb.vehiclerouting.service.location.LocationService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DemoServiceTest {

    @Mock
    private LocationService locationService;
    @Mock
    private DataSetMarshaller dataSetMarshaller;
    @Mock
    private LocationRepository locationRepository;
    @Mock
    private RoutingProblemList routingProblems;
    @InjectMocks
    private DemoService demoService;

    @Captor
    private ArgumentCaptor<RoutingProblem> routingProblemCaptor;

    private final Location depot = new Location(1, LatLng.valueOf(1.0, 7), "Depot");
    private final List<Location> visits = Arrays.asList(new Location(2, LatLng.valueOf(2.0, 9), "Visit"));
    private final String problemName = "Testing problem";
    private final RoutingProblem routingProblem = new RoutingProblem(problemName, depot, visits);

    @Before
    public void setUp() {
        when(locationService.createLocation(any(), anyString())).thenReturn(true);
        when(routingProblems.all()).thenReturn(Arrays.asList(routingProblem));
        when(routingProblems.byName(problemName)).thenReturn(routingProblem);
    }

    @Test
    public void demos_should_return_routing_problems() {
        Collection<RoutingProblem> problems = demoService.demos();
        assertThat(problems).containsExactly(routingProblem);
    }

    @Test
    public void loadDemo() {
        demoService.loadDemo(problemName);
        verify(locationService, times(routingProblem.getVisits().size() + 1)).createLocation(any(LatLng.class), anyString());
    }

    @Test
    public void retry_when_adding_location_fails() {
        when(locationService.createLocation(any(), anyString())).thenReturn(false);
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> demoService.loadDemo(problemName))
                .withMessageContaining(routingProblem.getDepot().getLatLng().toString());
        verify(locationService, times(DemoService.MAX_TRIES)).createLocation(any(LatLng.class), anyString());
    }

    @Test
    public void export() {
        Location depot = new Location(0, LatLng.valueOf(1.0, 2.0), "Depot");
        Location visit1 = new Location(1, LatLng.valueOf(11.0, 22.0), "Visit 1");
        Location visit2 = new Location(2, LatLng.valueOf(22.0, 33.0), "Visit 2");
        when(locationRepository.locations()).thenReturn(Arrays.asList(depot, visit1, visit2));

        demoService.exportDataSet();

        verify(dataSetMarshaller).marshall(routingProblemCaptor.capture());
        RoutingProblem routingProblem = routingProblemCaptor.getValue();

        assertThat(routingProblem.getName()).isNotNull();
        assertThat(routingProblem.getDepot()).isEqualTo(depot);
        assertThat(routingProblem.getVisits()).containsExactly(visit1, visit2);
    }
}
