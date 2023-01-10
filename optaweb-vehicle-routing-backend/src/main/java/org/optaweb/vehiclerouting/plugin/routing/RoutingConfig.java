package org.optaweb.vehiclerouting.plugin.routing;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.optaweb.vehiclerouting.Profiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphhopper.config.CHProfile;
import com.graphhopper.config.Profile;
import com.graphhopper.reader.osm.GraphHopperOSM;
import com.graphhopper.routing.util.CarFlagEncoder;
import com.graphhopper.routing.util.EncodingManager;

import io.quarkus.arc.DefaultBean;
import io.quarkus.arc.profile.UnlessBuildProfile;
import io.quarkus.arc.properties.IfBuildProperty;

/**
 * Configuration bean that creates a GraphHopper instance and allows to configure the path to OSM file
 * through environment.
 */
@Dependent
class RoutingConfig {

    private static final Logger logger = LoggerFactory.getLogger(RoutingConfig.class);

    private final Path osmDir;
    private final Path osmFile;
    private final Optional<String> osmDownloadUrl;
    private final Path graphHopperDir;
    private final Path graphDir;

    @Inject
    RoutingConfig(RoutingProperties routingProperties) {
        osmDir = Paths.get(routingProperties.osmDir()).toAbsolutePath();
        osmFile = osmDir.resolve(routingProperties.osmFile()).toAbsolutePath();
        osmDownloadUrl = routingProperties.osmDownloadUrl();
        graphHopperDir = Paths.get(routingProperties.ghDir());
        String regionName = routingProperties.osmFile().replaceFirst("\\.osm\\.pbf$", "");
        graphDir = graphHopperDir.resolve(regionName).toAbsolutePath();
    }

    /**
     * Avoids creating a real GraphHopper instance when running a @QuarkusTest.
     *
     * @return real GraphHopper
     */
    @UnlessBuildProfile(Profiles.TEST)
    @IfBuildProperty(name = "app.routing.engine", stringValue = "GRAPHHOPPER", enableIfMissing = true)
    @Produces
    @DefaultBean
    GraphHopperOSM graphHopper() {
        GraphHopperOSM graphHopper = ((GraphHopperOSM) new GraphHopperOSM().forServer());
        graphHopper.setGraphHopperLocation(graphDir.toString());

        if (graphDirIsNotEmpty()) {
            logger.info("Loading existing GraphHopper graph from: {}", graphDir);
        } else {
            if (Files.notExists(osmFile)) {
                initDirs();

                if (!osmDownloadUrl.isPresent() || osmDownloadUrl.get().trim().isEmpty()) {
                    throw new IllegalStateException(
                            "The osmFile (" + osmFile + ") does not exist"
                                    + " and no download URL was provided.\n"
                                    + "Download the OSM file from https://download.geofabrik.de/ first"
                                    + " or provide an OSM file URL"
                                    + " using the app.routing.osm-download-url property.");
                }
                downloadOsmFile(osmDownloadUrl.get(), osmFile);
            }
            logger.info("Importing OSM file: {}", osmFile);
            graphHopper.setOSMFile(osmFile.toString());
        }

        /*
         * FlagEncoder defines how a value (like speed or direction) converts to a flag, which is stored in an edge of the
         * graph. This is vehicle-specific, so we need to register one encoder for each vehicle type we're going to use.
         *
         * Assuming all the vehicles delivering packages to customers are cars, we're only going to need the CarFlagEncoder.
         */
        EncodingManager encodingManager = EncodingManager.start().add(new CarFlagEncoder()).build();
        graphHopper.setEncodingManager(encodingManager);
        /*
         * Define a profile for each type of request that's going to be made at runtime. We're only going to ask for the fastest
         * route for a car, so we only need one profile.
         *
         * Change the weighting to "shortest" (and delete the graph directory to re-import it) to optimize for shortest routes.
         *
         * Add a second profile with "shortest" weighting (and delete the graph directory) to be able to change travel cost
         * optimization goal at runtime.
         */
        graphHopper.setProfiles(new Profile(Constants.GRAPHHOPPER_PROFILE).setVehicle("car").setWeighting("fastest"));
        /*
         * Quick overview of routing modes:
         *
         * Flexible mode:
         * - Dijkstra or A*
         * - able to change requirements per request
         * Speed mode:
         * - "Contraction Hierarchies" algorithm (CH)
         * - still Dijkstra but on a "shortcut graph"
         * Hybrid mode
         * - landmark algorithm
         * - flexible and fast
         *
         * See https://www.graphhopper.com/blog/2017/08/14/flexible-routing-15-times-faster/.
         */
        // Use CH for the only profile we have.
        graphHopper.getCHPreparationHandler().setCHProfiles(new CHProfile(Constants.GRAPHHOPPER_PROFILE));
        graphHopper.importOrLoad();
        logger.info("GraphHopper graph loaded");
        return graphHopper;
    }

    /**
     * Decide whether the graph can be loaded.
     *
     * @return true if the graph directory exists and is not empty
     */
    private boolean graphDirIsNotEmpty() {
        if (Files.notExists(graphDir)) {
            return false;
        }
        try (Stream<Path> graphDirFiles = Files.list(graphDir)) {
            // Defensive programming. Check if the graph dir is empty. That happens if the import fails
            // for example due to OutOfMemoryError.
            return graphDirFiles.findAny().isPresent();
        } catch (IOException e) {
            throw new RoutingEngineException("Cannot read contents of the graph directory (" + graphDir + ")", e);
        }
    }

    private void initDirs() {
        try {
            Files.createDirectories(osmDir);
            Files.createDirectories(graphHopperDir);
        } catch (IOException e) {
            throw new RoutingEngineException("Can't create directory for storing OSM download", e);
        }
    }

    static void downloadOsmFile(String urlString, Path osmFile) {
        HttpURLConnection con;
        URL url;
        try {
            url = new URL(urlString);
            con = (HttpURLConnection) url.openConnection();
        } catch (MalformedURLException e) {
            throw new RoutingEngineException("The OSM file URL is malformed", e);
        } catch (IOException e) {
            throw new RoutingEngineException("The OSM file cannot be downloaded", e);
        }
        try {
            con.setRequestMethod("GET");
        } catch (ProtocolException e) {
            throw new IllegalStateException("Can't set request method", e);
        }

        con.setConnectTimeout(10000);
        con.setReadTimeout(10000);

        logger.info("Downloading OSM file from {}", urlString);
        try {
            Files.copy(con.getInputStream(), osmFile);
        } catch (IOException e) {
            throw new RoutingEngineException("OSM file download failed", e);
        }
        logger.info("File saved to {}", osmFile);
    }
}
