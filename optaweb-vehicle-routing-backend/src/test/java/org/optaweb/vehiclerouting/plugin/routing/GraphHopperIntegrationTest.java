package org.optaweb.vehiclerouting.plugin.routing;

import static org.assertj.core.api.Assertions.assertThatCode;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.graphhopper.reader.osm.GraphHopperOSM;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.routing.util.FlagEncoderFactory;

class GraphHopperIntegrationTest {

    private static final String OSM_PBF = "planet_12.032,53.0171_12.1024,53.0491.osm.pbf";

    @Test
    void graphhopper_should_import_and_load_osm_file_successfully(@TempDir Path tempDir) {
        Path graphhopperDir = tempDir.resolve("graphhopper");
        GraphHopperOSM graphHopper = ((GraphHopperOSM) new GraphHopperOSM().forServer());
        graphHopper.setGraphHopperLocation(graphhopperDir.toString());
        graphHopper.setOSMFile(GraphHopperIntegrationTest.class.getResource(OSM_PBF).getFile());
        graphHopper.setEncodingManager(EncodingManager.create(FlagEncoderFactory.CAR));
        assertThatCode(graphHopper::importOrLoad).doesNotThrowAnyException();
    }
}
