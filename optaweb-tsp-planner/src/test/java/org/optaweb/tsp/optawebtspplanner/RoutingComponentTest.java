package org.optaweb.tsp.optawebtspplanner;

import java.math.BigDecimal;
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
import org.optaplanner.examples.tsp.domain.location.RoadLocation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RoutingComponentTest {

    private final PointList pointList = new PointList();
    private final RoadLocation from = new RoadLocation(0, 0.1, 0.1);
    private final RoadLocation to = new RoadLocation(1, 0.2, 0.2);
    @Mock
    private GraphHopperOSM graphHopper;
    @Mock
    private GHResponse ghResponse;
    @Mock
    private PathWrapper pathWrapper;

    private static Place place(double lat, double lng) {
        return new Place(BigDecimal.valueOf(lat), BigDecimal.valueOf(lng));
    }

    @Before
    public void setUp() {
        when(graphHopper.route(any(GHRequest.class))).thenReturn(ghResponse);
        when(ghResponse.getBest()).thenReturn(pathWrapper);
        when(pathWrapper.getPoints()).thenReturn(pointList);
    }

    @Test
    public void getDistance_should_return_graphhopper_distance() {
        // arrange
        RoutingComponent routing = new RoutingComponent(graphHopper);
        when(pathWrapper.getDistance()).thenReturn(Math.PI);

        // act & assert
        assertThat(routing.getDistance(from, to)).isEqualTo(Math.PI);
    }

    @Test
    public void getDistance_should_throw_exception_when_no_route_exists() {
        // arrange
        RoutingComponent routing = new RoutingComponent(graphHopper);
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
        RoutingComponent routing = new RoutingComponent(graphHopper);
        pointList.add(1, 1);
        pointList.add(2, 2);
        pointList.add(3, 3);

        // act & assert
        List<Place> route = routing.getRoute(place(0.1, 0.1), place(0.2, 0.2));
        assertThat(route).containsExactly(
                place(1, 1),
                place(2, 2),
                place(3, 3)
        );
    }
}
