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

package org.optaweb.vehiclerouting;

import com.graphhopper.reader.osm.GraphHopperOSM;
import org.mockito.Mockito;
import org.optaweb.vehiclerouting.service.route.RouteListener;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile(Profiles.TEST)
public class TestConfig {

    /**
     * Creates a GraphHopper mock that may be used when running a {@link SpringBootTest @SpringBootTest}.
     * @return mock GraphHopper
     */
    @Bean
    public GraphHopperOSM graphHopper() {
        return Mockito.mock(GraphHopperOSM.class);
    }

    /**
     * Creates a mock route listener to avoid things like touching database and WebSocket.
     * @return mock RouteListener
     */
    @Bean
    public RouteListener routeListener() {
        return Mockito.mock(RouteListener.class);
    }
}
