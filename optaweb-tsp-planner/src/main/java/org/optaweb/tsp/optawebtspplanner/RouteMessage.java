package org.optaweb.tsp.optawebtspplanner;

import java.util.List;

public class RouteMessage {

    private final String distance;
    private final List<Place> route;

    public RouteMessage(String distance, List<Place> route) {
        this.distance = distance;
        this.route = route;
    }

    public String getDistance() {
        return distance;
    }

    public List<Place> getRoute() {
        return route;
    }
}
