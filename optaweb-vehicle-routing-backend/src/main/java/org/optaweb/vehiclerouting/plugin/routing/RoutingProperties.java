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

import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@ConfigurationProperties("app.routing")
@Validated
class RoutingProperties {

    /**
     * Directory to read OSM files from.
     */
    private String osmDir = "local/openstreetmap";

    /**
     * Directory where GraphHopper graphs are stored.
     */
    private String ghDir = "local/graphhopper";

    /**
     * OpenStreetMap file name.
     */
    @NotNull
    private String osmFile;

    /**
     * URL of an .osm.pbf file that will be downloaded in case the file doesn't exist on the file system.
     */
    private String osmDownloadUrl;

    /**
     * Routing engine providing distances and paths.
     */
    private RoutingEngine engine;

    public String getOsmDir() {
        return osmDir;
    }

    public void setOsmDir(String osmDir) {
        this.osmDir = osmDir;
    }

    public String getGhDir() {
        return ghDir;
    }

    public void setGhDir(String ghDir) {
        this.ghDir = ghDir;
    }

    public String getOsmFile() {
        return osmFile;
    }

    public void setOsmFile(String osmFile) {
        this.osmFile = osmFile;
    }

    public String getOsmDownloadUrl() {
        return osmDownloadUrl;
    }

    public void setOsmDownloadUrl(String osmDownloadUrl) {
        this.osmDownloadUrl = osmDownloadUrl;
    }

    public RoutingEngine getEngine() {
        return engine;
    }

    public void setEngine(RoutingEngine engine) {
        this.engine = engine;
    }

    public enum RoutingEngine {
        AIR,
        GRAPHHOPPER
    }
}
