package org.optaweb.tsp.optawebtspplanner.routing;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.reader.osm.GraphHopperOSM;
import com.graphhopper.util.PointList;
import org.optaweb.tsp.optawebtspplanner.core.LatLng;
import org.springframework.stereotype.Component;

@Component
public class RoutingComponent {

    private final GraphHopperOSM graphHopper;

    RoutingComponent(GraphHopperOSM graphHopper) {
        this.graphHopper = graphHopper;
    }

    public List<LatLng> getRoute(LatLng from, LatLng to) {
        GHRequest segmentRq = new GHRequest(
                from.getLatitude().doubleValue(),
                from.getLongitude().doubleValue(),
                to.getLatitude().doubleValue(),
                to.getLongitude().doubleValue());
        PointList points = graphHopper.route(segmentRq).getBest().getPoints();
        return StreamSupport.stream(points.spliterator(), false)
                .map(ghPoint3D -> new LatLng(BigDecimal.valueOf(ghPoint3D.lat), BigDecimal.valueOf(ghPoint3D.lon)))
                .collect(Collectors.toList());
    }

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
