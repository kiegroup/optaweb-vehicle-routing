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

package org.acme.getting.started;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

import org.mockito.Mockito;
import org.optaweb.vehiclerouting.Profiles;
import org.optaweb.vehiclerouting.service.distance.DistanceCalculator;
import org.optaweb.vehiclerouting.service.region.Region;
import org.optaweb.vehiclerouting.service.route.RouteListener;
import org.optaweb.vehiclerouting.service.route.Router;

import com.graphhopper.reader.osm.GraphHopperOSM;

import io.quarkus.arc.AlternativePriority;
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
    @AlternativePriority(1)
    @Produces
    public RouteListener routeListener() {
        return Mockito.mock(RouteListener.class);
    }

    @IfBuildProfile(Profiles.TEST)
    @AlternativePriority(1)
    @Produces
    public Router router() {
        return Mockito.mock(Router.class);
    }

    @IfBuildProfile(Profiles.TEST)
    @AlternativePriority(1)
    @Produces
    public Region region() {
        return Mockito.mock(Region.class);
    }

    @IfBuildProfile(Profiles.TEST)
    @AlternativePriority(1)
    @Produces
    public DistanceCalculator distanceCalculator() {
        return Mockito.mock(DistanceCalculator.class);
    }

}
