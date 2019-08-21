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

package org.optaweb.vehiclerouting.plugin.websocket;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.Location;
import org.springframework.boot.test.json.JacksonTester;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.optaweb.vehiclerouting.plugin.websocket.PortableCoordinates.fromCoordinates;
import static org.optaweb.vehiclerouting.plugin.websocket.PortableLocation.fromLocation;

class PortableRouteTest {

    private JacksonTester<PortableRoute> json;

    @BeforeEach
    void setUp() {
        // This initializes the json field
        JacksonTester.initFields(this, new ObjectMapper());
    }

    @Test
    void marshal_to_json() throws IOException {
        PortableVehicle vehicle = new PortableVehicle(13, "Vehicle", 45317);
        PortableLocation depot = visit(8, 42.6501218, -71.8835449, "Test depot");
        PortableLocation visit1 = visit(100, 42.7066596, -72.4934873, "Visit 1");
        PortableLocation visit2 = visit(200, 42.5543343, -71.4438280, "Visit 2");

        PortableRoute portableRoute = new PortableRoute(
                vehicle,
                depot,
                asList(visit1, visit2),
                asList(
                        asList(
                                coordinates(42.65005, -71.88522),
                                coordinates(42.64997, -71.88527)
                        ),
                        asList(
                                coordinates(42.64994, -71.88537),
                                coordinates(42.64994, -71.88542)
                        )
                )
        );
        assertThat(json.write(portableRoute)).isStrictlyEqualToJson("portable-route.json");
    }

    private static PortableLocation visit(long id, double latitude, double longitude, String description) {
        return fromLocation(new Location(id, Coordinates.valueOf(latitude, longitude), description));
    }

    private static PortableCoordinates coordinates(double latitude, double longitude) {
        return fromCoordinates(Coordinates.valueOf(latitude, longitude));
    }
}
