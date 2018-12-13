package org.optaweb.tsp.optawebtspplanner.routing;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.Test;
import org.optaplanner.examples.tsp.domain.location.RoadLocation;
import org.optaweb.tsp.optawebtspplanner.core.LatLng;
import org.optaweb.tsp.optawebtspplanner.core.Location;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class DistanceMatrixTest {

    @Test
    public void test() {
        DistanceMatrix distanceMatrix = new DistanceMatrix(new MockRouting());

        Location l0 = location(100, 0);
        Location l1 = location(111, 1);
        Location l9neg = location(321, -9);

        Map<RoadLocation, Double> mapL0 = distanceMatrix.addLocation(l0);
        assertThat(mapL0.size()).isEqualTo(1);

        // distance to self
        assertThat(mapL0.get(roadLocation(l0))).isEqualTo(0.0);
        // distance to not yet registered location
        assertThatThrownBy(() -> mapL0.get(roadLocation(l1))).isInstanceOf(IllegalArgumentException.class);

        Map<RoadLocation, Double> mapL1 = distanceMatrix.addLocation(l1);
        // distance to self
        assertThat(mapL1.get(roadLocation(l1))).isEqualTo(0.0);

        // distance 0 <-> 1
        assertThat(mapL1.get(roadLocation(l0))).isEqualTo(-1.0);
        assertThat(mapL0.get(roadLocation(l1))).isEqualTo(1.0);

        Map<RoadLocation, Double> mapL9 = distanceMatrix.addLocation(l9neg);

        // distances -9 -> {0, 1}
        assertThat(mapL9.get(roadLocation(l0))).isEqualTo(9.0);
        assertThat(mapL9.get(roadLocation(l1))).isEqualTo(10.0);
        // distances {0, 1} -> -9
        assertThat(mapL0.get(roadLocation(l9neg))).isEqualTo(-9.0);
        assertThat(mapL1.get(roadLocation(l9neg))).isEqualTo(-10.0);

        // distance map sizes
        assertThat(mapL0.size()).isEqualTo(3);
        assertThat(mapL1.size()).isEqualTo(3);
        assertThat(mapL9.size()).isEqualTo(3);
    }

    private static Location location(long id, int longitude) {
        return new Location(id, new LatLng(BigDecimal.ZERO, BigDecimal.valueOf(longitude)));
    }

    private static RoadLocation roadLocation(Location location) {
        RoadLocation roadLocation = new RoadLocation();
        roadLocation.setId(location.getId());
        return roadLocation;
    }

    private static class MockRouting extends RoutingComponent {

        public MockRouting() {
            super(null);
        }

        @Override
        public double getDistance(LatLng from, LatLng to) {
            // imagine 1D space (all locations on equator)
            return to.getLongitude().doubleValue() - from.getLongitude().doubleValue();
        }
    }
}
