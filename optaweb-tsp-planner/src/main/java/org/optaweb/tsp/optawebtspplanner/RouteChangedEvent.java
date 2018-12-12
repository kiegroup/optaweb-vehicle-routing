package org.optaweb.tsp.optawebtspplanner;

import java.util.List;

import org.optaweb.tsp.optawebtspplanner.network.Place;
import org.springframework.context.ApplicationEvent;

public class RouteChangedEvent extends ApplicationEvent {

    private final String distance;
    private final List<Place> route;

    /**
     * Create a new ApplicationEvent.
     * @param source the object on which the event initially occurred (never {@code null})
     * @param distance route distance
     * @param route list of locations
     */
    public RouteChangedEvent(Object source, String distance, List<Place> route) {
        super(source);
        this.route = route;
        this.distance = distance;
    }

    public List<Place> getRoute() {
        return route;
    }

    public String getDistance() {
        return distance;
    }
}
