package org.optaweb.tsp.optawebtspplanner.routing;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.optaplanner.examples.tsp.domain.location.RoadLocation;
import org.optaweb.tsp.optawebtspplanner.core.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DistanceMatrix {

    private final RoutingComponent routing;
    private final Map<Location, DistanceMap> matrix = new HashMap<>();

    @Autowired
    public DistanceMatrix(RoutingComponent routing) {
        this.routing = routing;
    }

    // FIXME don't use Planning domain
    public Map<RoadLocation, Double> addLocation(Location location) {
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

    private static class DistanceMap implements Map<RoadLocation, Double> {

        private final Map<Long, Double> distanceMap = new HashMap<>(100);

        public Double put(Long id, Double distance) {
            return distanceMap.put(id, distance);
        }

        @Override
        public int size() {
            return distanceMap.size();
        }

        @Override
        public boolean isEmpty() {
            return distanceMap.isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            return (key instanceof RoadLocation) && distanceMap.containsKey(((RoadLocation) key).getId());
        }

        @Override
        public boolean containsValue(Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Double get(Object key) {
            return (key instanceof RoadLocation)
                    ? distanceMap.get(((RoadLocation) key).getId())
                    : null;
        }

        @Override
        public Double put(RoadLocation key, Double value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Double remove(Object key) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void putAll(Map<? extends RoadLocation, ? extends Double> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<RoadLocation> keySet() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Collection<Double> values() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<Entry<RoadLocation, Double>> entrySet() {
            throw new UnsupportedOperationException();
        }
    }
}
