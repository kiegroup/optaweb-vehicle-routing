/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaweb.vehiclerouting.plugin.planner.domain;

import org.junit.jupiter.api.Test;
import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.Location;

import static org.assertj.core.api.Assertions.assertThat;

class PlanningLocationFactoryTest {

    @Test
    void planning_location_should_have_same_properties_as_domain_location() {
        long id = 344;
        double latitude = -20.5;
        double longitude = 11.7;
        Location location = new Location(id, Coordinates.valueOf(latitude, longitude));
        PlanningLocation planningLocation = PlanningLocationFactory.fromDomain(location);
        assertThat(planningLocation.getId()).isEqualTo(id);
        assertThat(planningLocation.getLatitude()).isEqualTo(latitude);
        assertThat(planningLocation.getLongitude()).isEqualTo(longitude);
    }
}
