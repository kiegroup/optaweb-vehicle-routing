package org.optaweb.tsp.optawebtspplanner.plugin.routing;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.reader.osm.GraphHopperOSM;
import com.graphhopper.util.PointList;
import org.optaweb.tsp.optawebtspplanner.domain.LatLng;
import org.optaweb.tsp.optawebtspplanner.interactor.route.Router;
import org.springframework.stereotype.Component;

/**
 * Provides geographical information needed for route optimization.
 */
@Component
public class RouterImpl implements Router {

    private final GraphHopperOSM graphHopper;

    RouterImpl(GraphHopperOSM graphHopper) {
        this.graphHopper = graphHopper;
    }

    @Override
    public List<LatLng> getRoute(LatLng from, LatLng to) {
        GHRequest segmentRq = new GHRequest(
                from.getLatitude().doubleValue(),
                from.getLongitude().doubleValue(),
                to.getLatitude().doubleValue(),
                to.getLongitude().doubleValue());
        PointList points = graphHopper.route(segmentRq).getBest().getPoints();
        return StreamSupport.stream(points.spliterator(), false)
                .map(ghPoint3D -> LatLng.valueOf(ghPoint3D.lat, ghPoint3D.lon))
                .collect(Collectors.toList());
    }

    @Override
    public double getDistance(LatLng from, LatLng to) {
        GHRequest ghRequest = new GHRequest(
                from.getLatitude().doubleValue(),
                from.getLongitude().doubleValue(),
                to.getLatitude().doubleValue(),
                to.getLongitude().doubleValue());
        GHResponse ghResponse = graphHopper.route(ghRequest);
        // TODO return wrapper that can hold both the result and error explanation instead of throwing exception
        if (ghResponse.hasErrors()) {
            throw new RuntimeException("No route", ghResponse.getErrors().get(0));
        }
        return ghResponse.getBest().getDistance();
    }
}
