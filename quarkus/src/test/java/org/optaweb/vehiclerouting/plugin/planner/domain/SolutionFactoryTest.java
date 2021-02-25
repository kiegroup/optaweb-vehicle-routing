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

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;

class SolutionFactoryTest {

    @Test
    void empty_solution_should_be_empty() {
        VehicleRoutingSolution solution = SolutionFactory.emptySolution();
        assertThat(solution.getVisitList()).isEmpty();
        assertThat(solution.getDepotList()).isEmpty();
        assertThat(solution.getVehicleList()).isEmpty();
        assertThat(solution.getScore()).isEqualTo(HardSoftLongScore.ZERO);
    }

    @Test
    void solution_created_from_vehicles_depot_and_visits_should_be_consistent() {
        PlanningVehicle vehicle = new PlanningVehicle();

        PlanningLocation depotLocation = PlanningLocationFactory.testLocation(1);
        PlanningDepot depot = new PlanningDepot(depotLocation);

        PlanningVisit visit = PlanningVisitFactory.testVisit(2);

        VehicleRoutingSolution solutionWithDepot = SolutionFactory.solutionFromVisits(
                singletonList(vehicle),
                depot,
                singletonList(visit));
        assertThat(solutionWithDepot.getVehicleList()).containsExactly(vehicle);
        assertThat(vehicle.getDepot()).isEqualTo(depot);
        assertThat(solutionWithDepot.getDepotList()).containsExactly(depot);
        assertThat(solutionWithDepot.getVisitList()).hasSize(1);
        assertThat(solutionWithDepot.getVisitList()).containsExactly(visit);
        assertThat(solutionWithDepot.getVisitList().get(0).getLocation()).isEqualTo(visit.getLocation());

        VehicleRoutingSolution solutionWithNoDepot = SolutionFactory.solutionFromVisits(
                singletonList(vehicle),
                null,
                emptyList());
        assertThat(solutionWithNoDepot.getDepotList()).isEmpty();
    }
}
