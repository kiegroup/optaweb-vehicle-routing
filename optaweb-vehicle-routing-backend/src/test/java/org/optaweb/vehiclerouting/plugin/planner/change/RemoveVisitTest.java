package org.optaweb.vehiclerouting.plugin.planner.change;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVehicleFactory.testVehicle;
import static org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVisitFactory.testVisit;

import org.junit.jupiter.api.Test;
import org.optaplanner.test.api.solver.change.MockProblemChangeDirector;
import org.optaweb.vehiclerouting.plugin.planner.MockSolver;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVisit;
import org.optaweb.vehiclerouting.plugin.planner.domain.SolutionFactory;
import org.optaweb.vehiclerouting.plugin.planner.domain.VehicleRoutingSolution;

class RemoveVisitTest {

    @Test
    void remove_last_visit() {
        VehicleRoutingSolution solution = SolutionFactory.emptySolution();

        PlanningVisit removedVisit = testVisit(1);
        PlanningVisit otherVisit = testVisit(2);
        solution.getVisitList().add(otherVisit);
        solution.getVisitList().add(removedVisit);

        // V -> other -> removed
        otherVisit.setPreviousStandstill(testVehicle(10));
        otherVisit.setNextVisit(removedVisit);
        removedVisit.setPreviousStandstill(otherVisit);

        MockSolver<VehicleRoutingSolution> mockSolver = MockSolver.build(solution);
        mockSolver.whenLookingUp(removedVisit).thenReturn(removedVisit);

        // do change
        mockSolver.addProblemChange(new RemoveVisit(removedVisit));

        mockSolver.verifyEntityRemoved(removedVisit);
        assertThat(solution.getVisitList()).containsExactly(otherVisit);
    }

    @Test
    void remove_middle_visit() {
        VehicleRoutingSolution solution = SolutionFactory.emptySolution();

        PlanningVisit firstVisit = testVisit(1);
        PlanningVisit middleVisit = testVisit(2);
        PlanningVisit lastVisit = testVisit(3);
        solution.getVisitList().add(firstVisit);
        solution.getVisitList().add(lastVisit);
        solution.getVisitList().add(middleVisit);

        // V -> first -> removed -> last
        firstVisit.setPreviousStandstill(testVehicle(1));
        firstVisit.setNextVisit(middleVisit);
        middleVisit.setPreviousStandstill(firstVisit);
        middleVisit.setNextVisit(lastVisit);
        lastVisit.setPreviousStandstill(middleVisit);

        PlanningVisit removedVisit = testVisit(2);

        MockSolver<VehicleRoutingSolution> mockSolver = MockSolver.build(solution);
        mockSolver.whenLookingUp(removedVisit).thenReturn(middleVisit);

        // do change
        mockSolver.addProblemChange(new RemoveVisit(removedVisit));

        mockSolver.verifyVariableChanged(lastVisit, "previousStandstill");
        mockSolver.verifyEntityRemoved(removedVisit);

        assertThat(solution.getVisitList())
                .hasSize(2)
                .containsOnly(firstVisit, lastVisit);

        // V -> first -> removed -> last
        assertThat(lastVisit.getPreviousStandstill()).isEqualTo(firstVisit);
    }

    @Test
    void fail_fast_if_working_solution_visit_list_does_not_contain_working_visit() {
        VehicleRoutingSolution solution = SolutionFactory.emptySolution();

        long removedId = 111L;
        PlanningVisit removedVisit = testVisit(removedId);
        long wrongId = 222L;
        PlanningVisit wrongVisit = testVisit(wrongId);
        wrongVisit.setPreviousStandstill(testVisit(10));
        removedVisit.setNextVisit(wrongVisit);
        solution.getVisitList().add(wrongVisit);

        // do change
        RemoveVisit removeVisit = new RemoveVisit(removedVisit);
        assertThatIllegalStateException()
                .isThrownBy(() -> removeVisit.doChange(solution, new MockProblemChangeDirector()))
                .withMessageMatching(".*List .*" + wrongId + ".* doesn't contain the working.*" + removedId + ".*");
    }
}
