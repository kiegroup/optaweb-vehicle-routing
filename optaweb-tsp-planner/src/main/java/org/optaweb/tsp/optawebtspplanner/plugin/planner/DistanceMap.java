package org.optaweb.tsp.optawebtspplanner.plugin.planner;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.optaplanner.examples.tsp.domain.location.RoadLocation;
import org.optaweb.tsp.optawebtspplanner.core.Location;

/**
 * Temporary distance map implementation that allows to compute and store distances purely from
 * {@link org.optaweb.tsp.optawebtspplanner.core core domain} and later be queried using Planning domain.
 */
// TODO get rid of dependency on Planning domain
public class DistanceMap implements Map<RoadLocation, Double> {

    private final Location location;
    private final Map<Long, Double> distanceMap;

    public DistanceMap(Location location, Map<Long, Double> distanceMap) {
        this.location = location;
        this.distanceMap = distanceMap;
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
        return distanceMap.containsKey(((RoadLocation) key).getId());
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Double get(Object key) {
        if (!containsKey(key)) {
            throw new IllegalArgumentException("Distance from " + location + " to " + key
                    + " hasn't been recorded.\n"
                    + "We only know distances to " + distanceMap.keySet());
        }
        return distanceMap.get(((RoadLocation) key).getId());
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
