package org.optaweb.vehiclerouting.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Vehicle's {@link Route itinerary} enriched with detailed geographical description of the route.
 * This object contains data needed to visualize vehicle's route on a map.
 */
public class RouteWithTrack extends Route {

    private final List<List<Coordinates>> track;

    /**
     * Create a route with track. When route is empty (no visits), track must be empty too and vice-versa
     * (non-empty route must have a non-empty track).
     *
     * @param route vehicle's route (not {@code null})
     * @param track track going through all visits (not {@code null})
     */
    public RouteWithTrack(Route route, List<List<Coordinates>> track) {
        super(route.vehicle(), route.depot(), route.visits());
        this.track = new ArrayList<>(Objects.requireNonNull(track));
        if (route.visits().isEmpty() && !track.isEmpty() || !route.visits().isEmpty() && track.isEmpty()) {
            throw new IllegalArgumentException("Route and track must be either both empty or both non-empty");
        }
    }

    /**
     * Vehicle's track that goes from vehicle's depot through all visits and returns to the depot.
     *
     * @return vehicle's track (not {@code null})
     */
    public List<List<Coordinates>> track() {
        return Collections.unmodifiableList(track);
    }
}
