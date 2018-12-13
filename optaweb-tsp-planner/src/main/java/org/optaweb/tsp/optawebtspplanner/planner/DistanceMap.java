package org.optaweb.tsp.optawebtspplanner.planner;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.optaplanner.examples.tsp.domain.location.RoadLocation;

// TODO get rid of dependency on Planning domain
public class DistanceMap implements Map<RoadLocation, Double> {

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
