package org.optaweb.vehiclerouting;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

import org.mockito.Mockito;
import org.optaweb.vehiclerouting.service.route.RouteListener;

import com.graphhopper.reader.osm.GraphHopperOSM;

import io.quarkus.arc.profile.IfBuildProfile;
import io.quarkus.test.junit.QuarkusTest;

@Dependent
public class TestConfig {

    /**
     * Creates a GraphHopper mock that may be used when running a {@link QuarkusTest @QuarkusTest}.
     *
     * @return mock GraphHopper
     */
    @IfBuildProfile(Profiles.TEST)
    @Produces
    public GraphHopperOSM graphHopper() {
        return Mockito.mock(GraphHopperOSM.class);
    }

    /**
     * Creates a mock route listener to avoid things like touching database and WebSocket.
     *
     * @return mock RouteListener
     */
    @IfBuildProfile(Profiles.TEST)
    @Produces
    public RouteListener routeListener() {
        return Mockito.mock(RouteListener.class);
    }

}
