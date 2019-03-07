/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.optaweb.vehiclerouting.plugin.routing;

import java.util.Collections;
import java.util.List;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.PathWrapper;
import com.graphhopper.reader.osm.GraphHopperOSM;
import com.graphhopper.util.PointList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.optaweb.vehiclerouting.domain.LatLng;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RouterImplTest {

    private final PointList pointList = new PointList();
    private final LatLng from = LatLng.valueOf(-Double.MIN_VALUE, Double.MIN_VALUE);
    private final LatLng to = LatLng.valueOf(Double.MAX_VALUE, -Double.MAX_VALUE);
    @Mock
    private GraphHopperOSM graphHopper;
    @Mock
    private GHResponse ghResponse;
    @Mock
    private PathWrapper pathWrapper;

    @Before
    public void setUp() {
        when(graphHopper.route(any(GHRequest.class))).thenReturn(ghResponse);
        when(ghResponse.getBest()).thenReturn(pathWrapper);
        when(pathWrapper.getPoints()).thenReturn(pointList);
    }

    @Test
    public void getDistance_should_return_graphhopper_distance() {
        // arrange
        RouterImpl routing = new RouterImpl(graphHopper);
        when(pathWrapper.getDistance()).thenReturn(Math.PI);

        // act & assert
        assertThat(routing.getDistance(from, to)).isEqualTo(Math.PI);
    }

    @Test
    public void getDistance_should_throw_exception_when_no_route_exists() {
        // arrange
        RouterImpl routing = new RouterImpl(graphHopper);
        when(ghResponse.hasErrors()).thenReturn(true);
        when(ghResponse.getErrors()).thenReturn(Collections.singletonList(new RuntimeException()));

        // act & assert
        assertThatThrownBy(() -> routing.getDistance(from, to))
                .isNotInstanceOf(NullPointerException.class)
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No route");
    }

    @Test
    public void getRoute_should_return_graphopper_route() {
        // arrange
        RouterImpl routing = new RouterImpl(graphHopper);

        LatLng latLng1 = LatLng.valueOf(1, 1);
        LatLng latLng2 = LatLng.valueOf(Math.E, Math.PI);
        LatLng latLng3 = LatLng.valueOf(0.1, 1.0 / 3.0);

        pointList.add(latLng1.getLatitude().doubleValue(), latLng1.getLongitude().doubleValue());
        pointList.add(latLng2.getLatitude().doubleValue(), latLng2.getLongitude().doubleValue());
        pointList.add(latLng3.getLatitude().doubleValue(), latLng3.getLongitude().doubleValue());

        // act & assert
        List<LatLng> route = routing.getPath(from, to);
        assertThat(route).containsExactly(
                latLng1,
                latLng2,
                latLng3
        );
    }
}
