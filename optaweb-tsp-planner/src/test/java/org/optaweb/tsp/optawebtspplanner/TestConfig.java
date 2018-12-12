package org.optaweb.tsp.optawebtspplanner;

import com.graphhopper.reader.osm.GraphHopperOSM;
import org.mockito.Mockito;
import org.optaweb.tsp.optawebtspplanner.spring.Profiles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile(Profiles.TEST)
public class TestConfig {

    /**
     * Creates a GraphHopper mock that may be used when running a @SpringBootTest.
     * @return mock
     */
    @Bean
    public GraphHopperOSM graphHopper() {
        return Mockito.mock(GraphHopperOSM.class);
    }
}
