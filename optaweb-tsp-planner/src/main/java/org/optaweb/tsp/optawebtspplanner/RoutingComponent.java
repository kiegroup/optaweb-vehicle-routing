package org.optaweb.tsp.optawebtspplanner;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.reader.osm.GraphHopperOSM;
import com.graphhopper.util.PointList;
import org.optaplanner.examples.tsp.domain.location.RoadLocation;
import org.springframework.stereotype.Component;

@Component
public class RoutingComponent {

    private final GraphHopperOSM graphHopper;

    RoutingComponent(GraphHopperOSM graphHopper) {
        this.graphHopper = graphHopper;
    }

    List<Place> getRoute(Place from, Place to) {
        GHRequest segmentRq = new GHRequest(
                from.getLatitude().doubleValue(),
                from.getLongitude().doubleValue(),
                // "trick" to get N -> 0 distance at the end of the loop
                to.getLatitude().doubleValue(),
                to.getLongitude().doubleValue());
        PointList points = graphHopper.route(segmentRq).getBest().getPoints();
        return StreamSupport.stream(points.spliterator(), false)
                .map(ghPoint3D -> new Place(BigDecimal.valueOf(ghPoint3D.lat), BigDecimal.valueOf(ghPoint3D.lon)))
                .collect(Collectors.toList());
    }

    double getDistance(RoadLocation from, RoadLocation to) {
        GHRequest ghRequest = new GHRequest(
                from.getLatitude(),
                from.getLongitude(),
                to.getLatitude(),
                to.getLongitude());
        GHResponse ghResponse = graphHopper.route(ghRequest);
        // TODO return wrapper that can hold both the result and error explanation instead of throwing exception
        if (ghResponse.hasErrors()) {
            throw new RuntimeException("No route", ghResponse.getErrors().get(0));
        }
        return ghResponse.getBest().getDistance();
    }
}
