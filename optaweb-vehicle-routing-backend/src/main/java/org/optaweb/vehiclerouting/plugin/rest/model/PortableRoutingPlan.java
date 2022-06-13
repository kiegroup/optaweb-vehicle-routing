package org.optaweb.vehiclerouting.plugin.rest.model;

import java.util.List;

import org.optaweb.vehiclerouting.domain.RoutingPlan;

/**
 * {@link RoutingPlan} representation convenient for marshalling.
 */
public class PortableRoutingPlan {

    private final PortableDistance distance;
    private final List<PortableVehicle> vehicles;
    private final PortableLocation depot;
    private final List<PortableLocation> visits;
    private final List<PortableRoute> routes;

    PortableRoutingPlan(
            PortableDistance distance,
            List<PortableVehicle> vehicles,
            PortableLocation depot,
            List<PortableLocation> visits,
            List<PortableRoute> routes) {
        // TODO require non-null
        this.distance = distance;
        this.vehicles = vehicles;
        this.depot = depot;
        this.visits = visits;
        this.routes = routes;
    }

    public PortableDistance getDistance() {
        return distance;
    }

    public List<PortableVehicle> getVehicles() {
        return vehicles;
    }

    public PortableLocation getDepot() {
        return depot;
    }

    public List<PortableLocation> getVisits() {
        return visits;
    }

    public List<PortableRoute> getRoutes() {
        return routes;
    }
}
