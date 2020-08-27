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

/**
 * Creates {@link PlanningVisit} instances.
 */
public class PlanningVisitFactory {

    static final int DEFAULT_VISIT_DEMAND = 1;

    private PlanningVisitFactory() {
        throw new AssertionError("Utility class");
    }

    /**
     * Create visit with {@link #DEFAULT_VISIT_DEMAND}.
     *
     * @param location visit's location
     * @return new visit with the default demand
     */
    public static PlanningVisit fromLocation(PlanningLocation location) {
        return fromLocation(location, DEFAULT_VISIT_DEMAND);
    }

    /**
     * Create visit of a location with the given demand.
     *
     * @param location visit's location
     * @param demand visit's demand
     * @return visit with demand at the given location
     */
    public static PlanningVisit fromLocation(PlanningLocation location, int demand) {
        PlanningVisit visit = new PlanningVisit();
        visit.setId(location.getId());
        visit.setLocation(location);
        visit.setDemand(demand);
        return visit;
    }

    /**
     * Create a test visit with the given ID.
     *
     * @param id ID of the visit and its location
     * @return visit with an ID only
     */
    public static PlanningVisit testVisit(long id) {
        return fromLocation(PlanningLocationFactory.testLocation(id));
    }
}
