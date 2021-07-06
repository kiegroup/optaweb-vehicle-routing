/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
