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

import java.io.File;

import com.graphhopper.reader.osm.GraphHopperOSM;
import com.graphhopper.routing.util.EncodingManager;
import org.optaweb.vehiclerouting.Profiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Spring Bean producer that creates a GraphHopper instance and allows to configure the path to OSM file
 * through environment.
 */
@Configuration
public class RoutingConfig {

    private static final Logger logger = LoggerFactory.getLogger(RoutingConfig.class);
    private static final String OSM_DIR = "local/openstreetmap/";
    private static final String GH_DIR = "local/graphhopper/";

    private final RoutingProperties routingProperties;

    @Autowired
    public RoutingConfig(RoutingProperties routingProperties) {
        this.routingProperties = routingProperties;
    }

    /**
     * Avoids creating real GraphHopper instance when running a @SpringBootTest.
     * @return real GraphHopper
     */
    @Profile(Profiles.NOT_TEST)
    @Bean
    public GraphHopperOSM graphHopper() {
        GraphHopperOSM graphHopper = ((GraphHopperOSM) new GraphHopperOSM().forServer());
        String osmPath = OSM_DIR + routingProperties.getOsmFile();
        logger.info("OSM file: {}", osmPath);
        if (!new File(osmPath).exists()) {
            throw new IllegalStateException(
                    "The osmPath (" + new File(osmPath).getAbsolutePath() + ") does not exist.\n"
                            + "Download the osm file from http://download.geofabrik.de/ first."
            );
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
