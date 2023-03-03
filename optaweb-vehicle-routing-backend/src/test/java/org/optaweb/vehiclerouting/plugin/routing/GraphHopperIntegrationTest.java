package org.optaweb.vehiclerouting.plugin.routing;

import static org.assertj.core.api.Assertions.assertThatCode;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.graphhopper.GraphHopper;
import com.graphhopper.config.Profile;

class GraphHopperIntegrationTest {

    private static final String OSM_PBF = "planet_12.032,53.0171_12.1024,53.0491.osm.pbf";

    @Test
    void graphhopper_should_import_and_load_osm_file_successfully(@TempDir Path tempDir) {
        Path graphhopperDir = tempDir.resolve("graphhopper");
        GraphHopper graphHopper = new GraphHopper();
        graphHopper.setGraphHopperLocation(graphhopperDir.toString());
        graphHopper.setOSMFile(GraphHopperIntegrationTest.class.getResource(OSM_PBF).getFile());
        graphHopper.setProfiles(new Profile(Constants.GRAPHHOPPER_PROFILE).setVehicle("car").setWeighting("fastest"));
        assertThatCode(graphHopper::importOrLoad).doesNotThrowAnyException();
    }
}
