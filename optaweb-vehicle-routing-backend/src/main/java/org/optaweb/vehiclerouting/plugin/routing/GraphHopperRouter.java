package org.optaweb.vehiclerouting.plugin.routing;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.StreamSupport;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.service.distance.DistanceCalculator;
import org.optaweb.vehiclerouting.service.distance.RoutingException;
import org.optaweb.vehiclerouting.service.region.BoundingBox;
import org.optaweb.vehiclerouting.service.region.Region;
import org.optaweb.vehiclerouting.service.route.Router;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.ResponsePath;
import com.graphhopper.reader.osm.GraphHopperOSM;
import com.graphhopper.util.PointList;
import com.graphhopper.util.shapes.BBox;

import io.quarkus.arc.properties.IfBuildProperty;

/**
 * Provides geographical information needed for route optimization.
 */
@ApplicationScoped
@IfBuildProperty(name = "app.routing.engine", stringValue = "GRAPHHOPPER", enableIfMissing = true)
class GraphHopperRouter implements Router, DistanceCalculator, Region {

    private final GraphHopperOSM graphHopper;

    @Inject
    GraphHopperRouter(GraphHopperOSM graphHopper) {
        this.graphHopper = graphHopper;
    }

    @Override
    public List<Coordinates> getPath(Coordinates from, Coordinates to) {
        PointList points = getBestRoute(from, to).getPoints();
        return StreamSupport.stream(points.spliterator(), false)
                .map(ghPoint3D -> Coordinates.of(ghPoint3D.lat, ghPoint3D.lon))
                .collect(toList());
    }

    @Override
    public long travelTimeMillis(Coordinates from, Coordinates to) {
        return getBestRoute(from, to).getTime();
    }

    private ResponsePath getBestRoute(Coordinates from, Coordinates to) {
        GHRequest request = new GHRequest(
                from.latitude().doubleValue(),
                from.longitude().doubleValue(),
                to.latitude().doubleValue(),
                to.longitude().doubleValue()).setProfile(Constants.GRAPHHOPPER_PROFILE);
        GHResponse response = graphHopper.route(request);
        // TODO return wrapper that can hold both the result and error explanation instead of throwing exception
        if (response.hasErrors()) {
            throw new RoutingException("No route from (" + from + ") to (" + to + ")", response.getErrors().get(0));
        }
        return response.getBest();
    }

    @Override
    public BoundingBox getBounds() {
        BBox bounds = graphHopper.getGraphHopperStorage().getBounds();
        return new BoundingBox(
                Coordinates.of(bounds.minLat, bounds.minLon),
                Coordinates.of(bounds.maxLat, bounds.maxLon));
    }
}
