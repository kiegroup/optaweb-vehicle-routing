package org.optaweb.vehiclerouting.plugin.rest.model;

import java.util.Objects;

import org.optaweb.vehiclerouting.domain.RoutingProblem;

/**
 * Information about a {@link RoutingProblem routing problem instance}.
 */
public class RoutingProblemInfo {

    private final String name;
    private final int visits;

    public RoutingProblemInfo(String name, int visits) {
        this.name = Objects.requireNonNull(name);
        this.visits = visits;
    }

    /**
     * Routing problem instance name.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Number of visits in the routing problem instance.
     *
     * @return number of visits
     */
    public int getVisits() {
        return visits;
    }
}
