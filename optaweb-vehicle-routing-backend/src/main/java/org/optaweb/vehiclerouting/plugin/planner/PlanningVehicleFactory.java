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

import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVehicle;

/**
 * Creates {@link PlanningVehicle} instances.
 */
class PlanningVehicleFactory {

    private PlanningVehicleFactory() {
        throw new AssertionError("Utility class");
    }

    /**
     * Create planning vehicle from domain vehicle.
     * @param domainVehicle domain vehicle
     * @return planning vehicle
     */
    static PlanningVehicle fromDomain(org.optaweb.vehiclerouting.domain.Vehicle domainVehicle) {
        return vehicle(domainVehicle.id(), domainVehicle.capacity());
    }

    /**
     * Create vehicle with zero capacity.
     * @param id vehicle's ID
     * @return new vehicle with zero capacity
     */
    static PlanningVehicle vehicle(long id) {
        return vehicle(id, 0);
    }

    private static PlanningVehicle vehicle(long id, int capacity) {
        PlanningVehicle vehicle = new PlanningVehicle();
        vehicle.setId(id);
        vehicle.setCapacity(capacity);
        return vehicle;
    }
}
