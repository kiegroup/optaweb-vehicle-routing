package org.optaweb.vehiclerouting.plugin.planner.change;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.optaweb.vehiclerouting.plugin.planner.MockSolver;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVehicle;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVehicleFactory;
import org.optaweb.vehiclerouting.plugin.planner.domain.SolutionFactory;
import org.optaweb.vehiclerouting.plugin.planner.domain.VehicleRoutingSolution;

class AddVehicleTest {

    @Test
    void add_vehicle_should_add_vehicle() {
        VehicleRoutingSolution solution = SolutionFactory.emptySolution();

        MockSolver<VehicleRoutingSolution> mockSolver = MockSolver.build(solution);

        PlanningVehicle vehicle = PlanningVehicleFactory.testVehicle(1);
        mockSolver.addProblemChange(new AddVehicle(vehicle));

        assertThat(solution.getVehicleList()).containsExactly(vehicle);
        mockSolver.verifyProblemFactAdded(vehicle);
    }
}
