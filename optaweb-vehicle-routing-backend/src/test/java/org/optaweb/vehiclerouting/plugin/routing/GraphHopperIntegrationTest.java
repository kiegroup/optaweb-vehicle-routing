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

import java.nio.file.Path;

import com.graphhopper.reader.osm.GraphHopperOSM;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.routing.util.FlagEncoderFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class GraphHopperIntegrationTest {

    private static final String OSM_PBF = "planet_12.032,53.0171_12.1024,53.0491.osm.pbf";

    @Test
    void graphhopper_should_import_and_load_osm_file_successfully(@TempDir Path tempDir) {
        Path graphhopperDir = tempDir.resolve("graphhopper");
        GraphHopperOSM graphHopper = ((GraphHopperOSM) new GraphHopperOSM().forServer());
        graphHopper.setGraphHopperLocation(graphhopperDir.toString());
        graphHopper.setOSMFile(GraphHopperIntegrationTest.class.getResource(OSM_PBF).getFile());
        graphHopper.setEncodingManager(EncodingManager.create(FlagEncoderFactory.CAR));
        graphHopper.importOrLoad();
    }
}
