package org.optaweb.tsp.optawebtspplanner;

import java.io.File;

import com.graphhopper.reader.osm.GraphHopperOSM;
import com.graphhopper.routing.util.EncodingManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class RoutingConfig {

    private static Logger logger = LoggerFactory.getLogger(RoutingConfig.class);

    /**
     * Avoids creating real GraphHopper instance when running a @SpringBootTest.
     * @return real GraphHopper
     */
    @Profile(Profiles.NOT_TEST)
    @Bean
    public GraphHopperOSM graphHopper() {
        GraphHopperOSM graphHopper = ((GraphHopperOSM) new GraphHopperOSM().forServer());
        String osmPath = "local/belgium-latest.osm.pbf";
        if (!new File(osmPath).exists()) {
            throw new IllegalStateException("The osmPath (" + new File(osmPath).getAbsolutePath() + ") does not exist.\n" +
                    "Download the osm file from http://download.geofabrik.de/ first.");
        }
        graphHopper.setOSMFile(osmPath);
        graphHopper.setGraphHopperLocation("local/" + osmPath.replaceFirst(".*/(.*)\\.osm\\.pbf$", "$1-gh"));
        graphHopper.setEncodingManager(new EncodingManager("car"));
        logger.info("GraphHopper loading...");
        graphHopper.importOrLoad();
        logger.info("GraphHopper loaded.");
        return graphHopper;
    }
}
