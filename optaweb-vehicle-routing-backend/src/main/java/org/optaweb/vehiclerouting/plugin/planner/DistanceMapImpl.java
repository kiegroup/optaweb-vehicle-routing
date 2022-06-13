package org.optaweb.vehiclerouting.plugin.planner;

import java.util.Objects;

import org.optaweb.vehiclerouting.plugin.planner.domain.DistanceMap;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningLocation;
import org.optaweb.vehiclerouting.service.location.DistanceMatrixRow;

/**
 * Provides distances to {@link PlanningLocation}s by reading from a {@link DistanceMatrixRow}.
 */
public class DistanceMapImpl implements DistanceMap {

    private final DistanceMatrixRow distanceMatrixRow;

    public DistanceMapImpl(DistanceMatrixRow distanceMatrixRow) {
        this.distanceMatrixRow = Objects.requireNonNull(distanceMatrixRow);
    }

    @Override
    public long distanceTo(PlanningLocation location) {
        return distanceMatrixRow.distanceTo(location.getId()).millis();
    }
}
