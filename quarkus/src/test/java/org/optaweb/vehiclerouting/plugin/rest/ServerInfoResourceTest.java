/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.optaweb.vehiclerouting.plugin.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.domain.RoutingProblem;
import org.optaweb.vehiclerouting.domain.Vehicle;
import org.optaweb.vehiclerouting.domain.VehicleFactory;
import org.optaweb.vehiclerouting.plugin.rest.model.PortableCoordinates;
import org.optaweb.vehiclerouting.plugin.rest.model.RoutingProblemInfo;
import org.optaweb.vehiclerouting.plugin.rest.model.ServerInfo;
import org.optaweb.vehiclerouting.service.demo.DemoService;
import org.optaweb.vehiclerouting.service.region.BoundingBox;
import org.optaweb.vehiclerouting.service.region.RegionService;

@ExtendWith(MockitoExtension.class)
class ServerInfoResourceTest {

    @Mock
    private RegionService regionService;
    @Mock
    private DemoService demoService;
    @InjectMocks
    private ServerInfoResource serverInfoResource;

    @Test
    void serverInfo() {
        // arrange
        List<String> countryCodes = Arrays.asList("XY", "WZ");
        when(regionService.countryCodes()).thenReturn(countryCodes);

        Coordinates southWest = Coordinates.valueOf(-1.0, -2.0);
        Coordinates northEast = Coordinates.valueOf(1.0, 2.0);
        BoundingBox boundingBox = new BoundingBox(southWest, northEast);
        when(regionService.boundingBox()).thenReturn(boundingBox);

        Location depot = new Location(1, Coordinates.valueOf(1.0, 7), "Depot");
        List<Location> visits = Arrays.asList(new Location(2, Coordinates.valueOf(2.0, 9), "Visit"));
        List<Vehicle> vehicles = Arrays.asList(VehicleFactory.testVehicle(1));
        String demoName = "Testing problem";
        RoutingProblem routingProblem = new RoutingProblem(demoName, vehicles, depot, visits);
        when(demoService.demos()).thenReturn(Arrays.asList(routingProblem));

        // act
        ServerInfo serverInfo = serverInfoResource.serverInfo();

        // assert
        assertThat(serverInfo.getCountryCodes()).isEqualTo(countryCodes);
        assertThat(serverInfo.getBoundingBox()).containsExactly(
                PortableCoordinates.fromCoordinates(southWest),
                PortableCoordinates.fromCoordinates(northEast));
        List<RoutingProblemInfo> demos = serverInfo.getDemos();
        Assertions.assertThat(demos).hasSize(1);
        RoutingProblemInfo demo = demos.get(0);
        assertThat(demo.getName()).isEqualTo(demoName);
        assertThat(demo.getVisits()).isEqualTo(visits.size());
    }
}
