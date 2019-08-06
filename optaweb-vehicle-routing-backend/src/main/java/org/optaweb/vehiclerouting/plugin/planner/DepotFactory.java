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

package org.optaweb.vehiclerouting.plugin.planner;

import org.optaplanner.examples.vehiclerouting.domain.Depot;
import org.optaplanner.examples.vehiclerouting.domain.location.Location;

/**
 * Creates {@link Depot} instances.
 */
class DepotFactory {

    private DepotFactory() {
        throw new AssertionError("Utility class");
    }

    /**
     * Create depot from location. The depot's ID will be the same as the location's ID.
     * @param location depot's location
     * @return the new depot
     */
    static Depot depot(Location location) {
        Depot depot = new Depot();
        depot.setId(location.getId());
        depot.setLocation(location);
        return depot;
    }
}
