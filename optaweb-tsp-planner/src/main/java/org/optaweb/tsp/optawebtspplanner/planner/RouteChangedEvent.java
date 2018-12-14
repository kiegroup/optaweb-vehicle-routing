package org.optaweb.tsp.optawebtspplanner.planner;

import java.util.List;

import org.optaweb.tsp.optawebtspplanner.core.Location;
import org.springframework.context.ApplicationEvent;

/**
 * Event published when the best route has been changed either by discovering a better route or changing
 * the set of locations.
 */
public class RouteChangedEvent extends ApplicationEvent {

    private final String distance;
    private final List<Location> route;

    /**
     * Create a new ApplicationEvent.
     * @param source the object on which the event initially occurred (never {@code null})
     * @param distance route distance
     * @param route list of locations
     */
    public RouteChangedEvent(Object source, String distance, List<Location> route) {
        super(source);
        this.route = route;
        this.distance = distance;
    }

    public List<Location> getRoute() {
        return route;
    }

    public String getDistance() {
        return distance;
    }
}
