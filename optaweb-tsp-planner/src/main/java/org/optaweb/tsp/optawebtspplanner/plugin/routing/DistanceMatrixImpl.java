package org.optaweb.tsp.optawebtspplanner.plugin.routing;

import java.util.HashMap;
import java.util.Map;

import org.optaweb.tsp.optawebtspplanner.domain.Location;
import org.optaweb.tsp.optawebtspplanner.interactor.location.DistanceMatrix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Keeps the information about distances between every pair of locations.
 */
@Component
public class DistanceMatrixImpl implements DistanceMatrix {

    private final RouterImpl router;
    private final Map<Location, Map<Long, Double>> matrix = new HashMap<>();

    @Autowired
    public DistanceMatrixImpl(RouterImpl router) {
        this.router = router;
    }

    @Override
    public synchronized void addLocation(Location location) {
        Map<Long, Double> distanceMap = new HashMap<>();
        distanceMap.put(location.getId(), 0.0);
        for (Map.Entry<Location, Map<Long, Double>> entry : matrix.entrySet()) {
            Location other = entry.getKey();
            distanceMap.put(other.getId(), router.getDistance(location.getLatLng(), other.getLatLng()));
            entry.getValue().put(location.getId(), router.getDistance(other.getLatLng(), location.getLatLng()));
        }
        matrix.put(location, distanceMap);
    }

    @Override
    public synchronized Map<Long, Double> getRow(Location location) {
        return matrix.get(location);
    }
}
