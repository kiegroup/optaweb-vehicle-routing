package org.optaweb.tsp.optawebtspplanner.plugin.routing;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.Test;
import org.optaweb.tsp.optawebtspplanner.domain.LatLng;
import org.optaweb.tsp.optawebtspplanner.domain.Location;

import static org.assertj.core.api.Assertions.assertThat;

public class DistanceMatrixImplTest {

    @Test
    public void test() {
        DistanceMatrixImpl distanceMatrix = new DistanceMatrixImpl(new MockRouting());

        Location l0 = location(100, 0);
        Location l1 = location(111, 1);
        Location l9neg = location(321, -9);

        distanceMatrix.addLocation(l0);
        Map<Long, Double> mapL0 = distanceMatrix.getRow(l0);
        assertThat(mapL0.size()).isEqualTo(1);

        // distance to self
        assertThat(mapL0.get(l0.getId())).isEqualTo(0.0);
        // distance to not yet registered location
        assertThat(mapL0).doesNotContainKeys(l1.getId());

        distanceMatrix.addLocation(l1);
        Map<Long, Double> mapL1 = distanceMatrix.getRow(l1);
        // distance to self
        assertThat(mapL1.get(l1.getId())).isEqualTo(0.0);

        // distance 0 <-> 1
        assertThat(mapL1.get(l0.getId())).isEqualTo(-1.0);
        assertThat(mapL0.get(l1.getId())).isEqualTo(1.0);

        distanceMatrix.addLocation(l9neg);
        Map<Long, Double> mapL9 = distanceMatrix.getRow(l9neg);

        // distances -9 -> {0, 1}
        assertThat(mapL9.get(l0.getId())).isEqualTo(9.0);
        assertThat(mapL9.get(l1.getId())).isEqualTo(10.0);
        // distances {0, 1} -> -9
        assertThat(mapL0.get(l9neg.getId())).isEqualTo(-9.0);
        assertThat(mapL1.get(l9neg.getId())).isEqualTo(-10.0);

        // distance map sizes
        assertThat(mapL0.size()).isEqualTo(3);
        assertThat(mapL1.size()).isEqualTo(3);
        assertThat(mapL9.size()).isEqualTo(3);
    }

    private static Location location(long id, int longitude) {
        return new Location(id, new LatLng(BigDecimal.ZERO, BigDecimal.valueOf(longitude)));
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
