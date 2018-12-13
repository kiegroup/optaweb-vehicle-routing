package org.optaweb.tsp.optawebtspplanner.planner;

import org.optaweb.tsp.optawebtspplanner.core.Location;

public interface DistanceMapProvider {

    DistanceMap getDistanceMap(Location location);
}
