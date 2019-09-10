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

package org.optaweb.vehiclerouting.plugin.planner.domain;

import org.junit.jupiter.api.Test;
import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.Location;

import static org.assertj.core.api.Assertions.assertThat;

class PlanningLocationTest {

    @Test
    void planning_location_should_have_same_latitude_and_longitude_as_domain_location() {
        Location domainLocation = new Location(1, Coordinates.valueOf(1.0, 0.1));
        PlanningLocation planningLocation = new PlanningLocation(domainLocation);
        assertThat(planningLocation.getId()).isEqualTo(domainLocation.id());
        assertThat(planningLocation.getLatitude()).isEqualTo(domainLocation.coordinates().latitude());
        assertThat(planningLocation.getLongitude()).isEqualTo(domainLocation.coordinates().longitude());
    }
}
