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
