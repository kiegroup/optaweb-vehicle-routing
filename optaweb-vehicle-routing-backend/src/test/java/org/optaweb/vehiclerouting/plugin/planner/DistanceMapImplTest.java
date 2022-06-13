package org.optaweb.vehiclerouting.plugin.planner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import org.junit.jupiter.api.Test;
import org.optaweb.vehiclerouting.domain.Distance;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningLocation;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningLocationFactory;
import org.optaweb.vehiclerouting.service.location.DistanceMatrixRow;

class DistanceMapImplTest {

    @Test
    void matrix_row_must_not_be_null() {
        assertThatNullPointerException().isThrownBy(() -> new DistanceMapImpl(null));
    }

    @Test
    void distance_map_should_return_value_from_distance_matrix_row() {
        PlanningLocation location2 = PlanningLocationFactory.testLocation(2);
        Distance distance = Distance.ofMillis(45000);
        DistanceMatrixRow matrixRow = locationId -> distance;
        DistanceMapImpl distanceMap = new DistanceMapImpl(matrixRow);
        assertThat(distanceMap.distanceTo(location2)).isEqualTo(distance.millis());
    }
}
