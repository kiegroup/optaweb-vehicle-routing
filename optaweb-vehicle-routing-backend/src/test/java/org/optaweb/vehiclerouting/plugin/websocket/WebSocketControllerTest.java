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

package org.optaweb.vehiclerouting.plugin.websocket;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.optaweb.vehiclerouting.domain.LatLng;
import org.optaweb.vehiclerouting.service.demo.DemoService;
import org.optaweb.vehiclerouting.service.location.LocationService;
import org.optaweb.vehiclerouting.service.route.RouteListener;
import org.optaweb.vehiclerouting.service.route.RoutingPlan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WebSocketControllerTest {

    @Mock
    private RouteListener routeListener;
    @Mock
    private RoutePublisherImpl routePublisher;
    @Mock
    private LocationService locationService;
    @Mock
    private DemoService demoService;
    @InjectMocks
    private WebSocketController webSocketController;

    private PortableRoutingPlan portableRoutingPlan;

    @Before
    public void setUp() {
        portableRoutingPlan = new PortableRoutingPlan("xy", null, Collections.emptyList());
        when(routeListener.getBestRoutingPlan()).thenReturn(RoutingPlan.empty());
        when(routePublisher.portable(any(RoutingPlan.class))).thenReturn(portableRoutingPlan);
    }

    @Test
    public void subscribe() {
        assertThat(webSocketController.subscribe()).isEqualTo(portableRoutingPlan);
    }

    @Test
    public void addLocation() {
        LatLng latLng = LatLng.valueOf(0.0, 1.0);
        PortableLocation request = new PortableLocation(321, latLng.getLatitude(), latLng.getLongitude());
        webSocketController.addLocation(request);
        verify(locationService).createLocation(latLng);
    }

    @Test
    public void removeLocation() {
        webSocketController.removeLocation(9L);
        verify(locationService).removeLocation(9);
    }

    @Test
    public void demo() {
        int demoSize = 4651;
        when(demoService.getDemoSize()).thenReturn(demoSize);
        assertThat(webSocketController.demo()).isEqualTo(demoSize);
        verify(demoService).loadDemo();
    }

    @Test
    public void clear() {
        webSocketController.clear();
        verify(locationService).clear();
    }
}
