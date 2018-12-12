package org.optaweb.tsp.optawebtspplanner.routing;

import java.io.File;

import com.graphhopper.reader.osm.GraphHopperOSM;
import com.graphhopper.routing.util.EncodingManager;
import org.optaweb.tsp.optawebtspplanner.spring.Profiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

@Configuration
public class RoutingConfig {

    private static final Logger logger = LoggerFactory.getLogger(RoutingConfig.class);
    private static final String OSM_DIR = "local/openstreetmap/";
    private static final String OSM_FILE_KEY = "osmfile";
    private static final String GH_DIR = "local/graphhopper/";

    private Environment env;

    @Autowired
    public RoutingConfig(Environment env) {
        this.env = env;
    }

    /**
     * Avoids creating real GraphHopper instance when running a @SpringBootTest.
     * @return real GraphHopper
     */
    @Profile(Profiles.NOT_TEST)
    @Bean
    public GraphHopperOSM graphHopper() {
        GraphHopperOSM graphHopper = ((GraphHopperOSM) new GraphHopperOSM().forServer());
        String osmPath = OSM_DIR + env.getProperty(OSM_FILE_KEY, "belgium-latest.osm.pbf");
        if (!new File(osmPath).exists()) {
            throw new IllegalStateException("The osmPath (" + new File(osmPath).getAbsolutePath() + ") does not exist.\n" +
                    "Download the osm file from http://download.geofabrik.de/ first.");
        }
        graphHopper.setOSMFile(osmPath);
        graphHopper.setGraphHopperLocation(GH_DIR + osmPath.replaceFirst(".*/(.*)\\.osm\\.pbf$", "$1"));
        graphHopper.setEncodingManager(new EncodingManager("car"));
        logger.info("GraphHopper loading...");
        graphHopper.importOrLoad();
        logger.info("GraphHopper loaded.");
        return graphHopper;
    }
}
