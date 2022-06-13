package org.optaweb.vehiclerouting.plugin.routing;

import java.util.Optional;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "app.routing")
interface RoutingProperties {

    /**
     * Directory to read OSM files from.
     */
    String osmDir();

    /**
     * Directory where GraphHopper graphs are stored.
     */
    String ghDir();

    /**
     * OpenStreetMap file name.
     */
    String osmFile();

    /**
     * URL of an .osm.pbf file that will be downloaded in case the file doesn't exist on the file system.
     */
    Optional<String> osmDownloadUrl();

    /**
     * Routing engine providing distances and paths.
     */
    RoutingEngine engine();

    enum RoutingEngine {
        AIR,
        GRAPHHOPPER
    }
}
