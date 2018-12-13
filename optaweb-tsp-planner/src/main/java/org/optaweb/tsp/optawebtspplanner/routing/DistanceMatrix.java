package org.optaweb.tsp.optawebtspplanner.routing;

import java.util.HashMap;
import java.util.Map;

import org.optaweb.tsp.optawebtspplanner.core.Location;
import org.optaweb.tsp.optawebtspplanner.planner.DistanceMap;
import org.optaweb.tsp.optawebtspplanner.planner.DistanceMapProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DistanceMatrix implements DistanceMapProvider {

    private final RoutingComponent routing;
    private final Map<Location, DistanceMap> matrix = new HashMap<>();

    @Autowired
    public DistanceMatrix(RoutingComponent routing) {
        this.routing = routing;
    }

    public DistanceMap addLocation(Location location) {
        DistanceMap distanceMap = new DistanceMap();
        distanceMap.put(location.getId(), 0.0);
        for (Map.Entry<Location, DistanceMap> entry : matrix.entrySet()) {
            Location other = entry.getKey();
            distanceMap.put(other.getId(), routing.getDistance(location.getLatLng(), other.getLatLng()));
            entry.getValue().put(location.getId(), routing.getDistance(other.getLatLng(), location.getLatLng()));
        }
        matrix.put(location, distanceMap);
        return distanceMap;
    }

    @Override
    public DistanceMap getDistanceMap(Location location) {
        return matrix.get(location);
    }
}
