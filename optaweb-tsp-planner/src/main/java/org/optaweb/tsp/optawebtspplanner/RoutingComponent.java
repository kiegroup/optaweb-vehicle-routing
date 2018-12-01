package org.optaweb.tsp.optawebtspplanner;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.graphhopper.GHRequest;
import com.graphhopper.reader.osm.GraphHopperOSM;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.util.PointList;
import org.optaplanner.examples.tsp.domain.location.RoadLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RoutingComponent {

    private static Logger logger = LoggerFactory.getLogger(TspMapController.class);
    private GraphHopperOSM graphHopper;

    public RoutingComponent() {
        String osmPath = "local/belgium-latest.osm.pbf";
        if (!new File(osmPath).exists()) {
            throw new IllegalStateException("The osmPath (" + new File(osmPath).getAbsolutePath() + ") does not exist.\n" +
                    "Download the osm file from http://download.geofabrik.de/ first.");
        }
        graphHopper = (GraphHopperOSM) new GraphHopperOSM().forServer();
        graphHopper.setOSMFile(osmPath);
        graphHopper.setGraphHopperLocation("local/" + osmPath.replaceFirst(".*/(.*)\\.osm\\.pbf$", "$1-gh"));
        graphHopper.setEncodingManager(new EncodingManager("car"));
        logger.info("GraphHopper loading...");
        graphHopper.importOrLoad();
        logger.info("GraphHopper loaded.");
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

    public Double getDistance(RoadLocation from, RoadLocation to) {
        GHRequest ghRequest = new GHRequest(
                from.getLatitude(),
                from.getLongitude(),
                to.getLatitude(),
                to.getLongitude());
        // TODO Optional or throwException
        return graphHopper.route(ghRequest).getBest().getDistance();
    }
}
