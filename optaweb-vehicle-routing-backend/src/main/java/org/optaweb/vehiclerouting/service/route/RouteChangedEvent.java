package org.optaweb.vehiclerouting.service.route;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.optaweb.vehiclerouting.domain.Distance;

/**
 * Event published when the routing plan has been updated either by discovering a better route or by a change
 * in the problem specification (vehicles, visits).
 */
public class RouteChangedEvent {

    private final Distance distance;
    private final List<Long> vehicleIds;
    private final Long depotId;
    private final List<Long> visitIds;
    private final Collection<ShallowRoute> routes;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     * @param distance total distance of all vehicle routes
     * @param vehicleIds vehicle IDs
     * @param depotId depot ID (may be {@code null} if there are no locations)
     * @param visitIds IDs of visits
     * @param routes vehicle routes
     */
    public RouteChangedEvent(
            Object source,
            Distance distance,
            List<Long> vehicleIds,
            Long depotId,
            List<Long> visitIds,
            Collection<ShallowRoute> routes) {
        this.distance = Objects.requireNonNull(distance);
        this.vehicleIds = Objects.requireNonNull(vehicleIds);
        this.depotId = depotId; // may be null (no depot)
        this.visitIds = Objects.requireNonNull(visitIds);
        this.routes = Objects.requireNonNull(routes);
    }

    /**
     * IDs of all vehicles.
     *
     * @return vehicle IDs
     */
    public List<Long> vehicleIds() {
        return vehicleIds;
    }

    /**
     * Routes of all vehicles.
     *
     * @return vehicle routes
     */
    public Collection<ShallowRoute> routes() {
        return routes;
    }

    /**
     * Routing plan distance.
     *
     * @return distance (never {@code null})
     */
    public Distance distance() {
        return distance;
    }

    /**
     * The depot ID.
     *
     * @return depot ID
     */
    public Optional<Long> depotId() {
        return Optional.ofNullable(depotId);
    }

    public List<Long> visitIds() {
        return visitIds;
    }
}
