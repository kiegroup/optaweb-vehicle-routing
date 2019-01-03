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

package org.optaweb.vehiclerouting.service.route;

import java.util.Arrays;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RouteListenerTest {

    @Mock
    private Router router;
    @Mock
    private RoutePublisher publisher;
    @Captor
    private ArgumentCaptor<Route> routeArgumentCaptor;
    @InjectMocks
    private RouteListener routeListener;

    @Before
    public void setUp() {
    }

    @Test
    public void onApplicationEvent() {
        final LatLng latLng1 = LatLng.valueOf(0.0, 0.1);
        final LatLng latLng2 = LatLng.valueOf(2.0, -0.2);
        final LatLng checkpoint12 = LatLng.valueOf(12, 12);
        final LatLng checkpoint21 = LatLng.valueOf(21, 21);
        List<LatLng> segment1 = Arrays.asList(latLng1, checkpoint12, latLng2);
        List<LatLng> segment2 = Arrays.asList(latLng2, checkpoint21, latLng1);
        when(router.getRoute(latLng1, latLng2)).thenReturn(segment1);
        when(router.getRoute(latLng2, latLng1)).thenReturn(segment2);

        final Location location1 = new Location(1, latLng1);
        final Location location2 = new Location(2, latLng2);
        final String distance = "xy";
        RouteChangedEvent event = new RouteChangedEvent(this, distance, Arrays.asList(location1, location2));

        routeListener.onApplicationEvent(event);
        verify(publisher).publish(routeArgumentCaptor.capture());

        Route route = routeArgumentCaptor.getValue();
        assertThat(route.getDistance()).isEqualTo(distance);
        assertThat(route.getRoute()).containsExactly(location1, location2);
        assertThat(route.getSegments()).containsExactly(segment1, segment2);

        assertThat(routeListener.getBestRoute()).isEqualTo(route);
    }

    @Test
    public void new_RouteListener_should_return_empty_best_route() {
        Route bestRoute = routeListener.getBestRoute();
        assertThat(bestRoute.getDistance()).isEqualTo("0");
        assertThat(bestRoute.getRoute()).isEmpty();
        assertThat(bestRoute.getSegments()).isEmpty();
    }
}
