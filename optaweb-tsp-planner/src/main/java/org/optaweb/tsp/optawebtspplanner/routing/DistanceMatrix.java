package org.optaweb.tsp.optawebtspplanner.routing;

import java.util.HashMap;
import java.util.Map;

import org.optaweb.tsp.optawebtspplanner.core.Location;
import org.optaweb.tsp.optawebtspplanner.planner.DistanceMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Keeps the information about distances between every pair of locations.
 * <p>
 * TODO Currently, the API is encumbered by usage of OptaPlanner VRP example code that works with
 * {@code Map<RoadLocation, Double>}.
 */
@Component
public class DistanceMatrix {

    private final RoutingComponent routing;
    private final Map<Location, DistanceMap> matrix = new HashMap<>();

    @Autowired
    public DistanceMatrix(RoutingComponent routing) {
        this.routing = routing;
    }

    // TODO get rid of dependency on planner
    public synchronized DistanceMap addLocation(Location location) {
        DistanceMap distanceMap = new DistanceMap(location);
        distanceMap.put(location.getId(), 0.0);
        for (Map.Entry<Location, DistanceMap> entry : matrix.entrySet()) {
            Location other = entry.getKey();
            distanceMap.put(other.getId(), routing.getDistance(location.getLatLng(), other.getLatLng()));
            entry.getValue().put(location.getId(), routing.getDistance(other.getLatLng(), location.getLatLng()));
        }
        matrix.put(location, distanceMap);
        return distanceMap;
    }
}
