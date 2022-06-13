package org.optaweb.vehiclerouting.plugin.planner.change;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.optaweb.vehiclerouting.plugin.planner.MockSolver;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVisit;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVisitFactory;
import org.optaweb.vehiclerouting.plugin.planner.domain.SolutionFactory;
import org.optaweb.vehiclerouting.plugin.planner.domain.VehicleRoutingSolution;

class AddVisitTest {

    @Test
    void add_visit_should_add_location_and_create_visit() {
        VehicleRoutingSolution solution = SolutionFactory.emptySolution();
        MockSolver<VehicleRoutingSolution> mockSolver = MockSolver.build(solution);

        PlanningVisit visit = PlanningVisitFactory.testVisit(1);
        mockSolver.addProblemChange(new AddVisit(visit));

        mockSolver.verifyEntityAdded(visit);
        assertThat(solution.getVisitList()).containsExactly(visit);
    }
}
