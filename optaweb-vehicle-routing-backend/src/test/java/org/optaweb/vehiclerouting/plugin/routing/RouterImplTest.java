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
import org.optaweb.vehiclerouting.domain.Coordinates;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RouterImplTest {

    private final PointList pointList = new PointList();
    private final Coordinates from = Coordinates.valueOf(-Double.MIN_VALUE, Double.MIN_VALUE);
    private final Coordinates to = Coordinates.valueOf(Double.MAX_VALUE, -Double.MAX_VALUE);
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
    public void travel_time_should_return_graphhopper_time() {
        // arrange
        RouterImpl routing = new RouterImpl(graphHopper);
        long travelTimeMillis = 135 * 60 * 60 * 1000;
        when(pathWrapper.getTime()).thenReturn(travelTimeMillis);

        // act & assert
        assertThat(routing.travelTimeMillis(from, to)).isEqualTo(travelTimeMillis);
    }

    @Test
    public void getDistance_should_throw_exception_when_no_route_exists() {
        // arrange
        RouterImpl routing = new RouterImpl(graphHopper);
        when(ghResponse.hasErrors()).thenReturn(true);
        when(ghResponse.getErrors()).thenReturn(Collections.singletonList(new RuntimeException()));

        // act & assert
        assertThatThrownBy(() -> routing.travelTimeMillis(from, to))
                .isNotInstanceOf(NullPointerException.class)
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No route");
    }

    @Test
    public void getRoute_should_return_graphhopper_route() {
        // arrange
        RouterImpl routing = new RouterImpl(graphHopper);

        Coordinates coordinates1 = Coordinates.valueOf(1, 1);
        Coordinates coordinates2 = Coordinates.valueOf(Math.E, Math.PI);
        Coordinates coordinates3 = Coordinates.valueOf(0.1, 1.0 / 3.0);

        pointList.add(coordinates1.getLatitude().doubleValue(), coordinates1.getLongitude().doubleValue());
        pointList.add(coordinates2.getLatitude().doubleValue(), coordinates2.getLongitude().doubleValue());
        pointList.add(coordinates3.getLatitude().doubleValue(), coordinates3.getLongitude().doubleValue());

        // act & assert
        List<Coordinates> route = routing.getPath(from, to);
        assertThat(route).containsExactly(
                coordinates1,
                coordinates2,
                coordinates3
        );
    }
}
