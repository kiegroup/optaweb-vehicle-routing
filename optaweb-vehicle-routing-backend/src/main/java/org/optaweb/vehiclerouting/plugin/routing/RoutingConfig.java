/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

import com.graphhopper.reader.osm.GraphHopperOSM;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.routing.util.FlagEncoderFactory;

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
        osmDir = Paths.get(routingProperties.getOsmDir()).toAbsolutePath();
        osmFile = osmDir.resolve(routingProperties.getOsmFile()).toAbsolutePath();
        osmDownloadUrl = routingProperties.getOsmDownloadUrl();
        graphHopperDir = Paths.get(routingProperties.getGhDir());
        String regionName = routingProperties.getOsmFile().replaceFirst("\\.osm\\.pbf$", "");
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
                                    + "Download the OSM file from http://download.geofabrik.de/ first"
                                    + " or provide an OSM file URL"
                                    + " using the app.routing.osm-download-url property.");
                }
                downloadOsmFile(osmDownloadUrl.get(), osmFile);
            }
            logger.info("Importing OSM file: {}", osmFile);
            graphHopper.setOSMFile(osmFile.toString());
        }

        graphHopper.setEncodingManager(EncodingManager.create(FlagEncoderFactory.CAR));
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
            // Defensive programming. Check if the graph dir is empty. That happens if the import fails for example
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
