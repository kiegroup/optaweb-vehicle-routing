package org.optaweb.vehiclerouting.plugin.planner.domain;

import java.util.Objects;

public class PlanningDepot {

    private final PlanningLocation location;

    public PlanningDepot(PlanningLocation location) {
        this.location = Objects.requireNonNull(location);
    }

    public long getId() {
        return location.getId();
    }

    public PlanningLocation getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "PlanningDepot{" +
                "location=" + location.getId() +
                '}';
    }
}
