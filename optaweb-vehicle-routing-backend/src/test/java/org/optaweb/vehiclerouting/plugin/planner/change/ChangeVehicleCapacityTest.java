package org.optaweb.vehiclerouting.plugin.planner.change;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.optaweb.vehiclerouting.plugin.planner.MockSolver;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVehicle;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVehicleFactory;
import org.optaweb.vehiclerouting.plugin.planner.domain.SolutionFactory;
import org.optaweb.vehiclerouting.plugin.planner.domain.VehicleRoutingSolution;

class ChangeVehicleCapacityTest {

    @Test
    void change_vehicle_capacity() {
        int oldCapacity = 100;
        int newCapacity = 50;

        MockSolver<VehicleRoutingSolution> mockSolver = MockSolver.build(SolutionFactory.emptySolution());

        PlanningVehicle workingVehicle = PlanningVehicleFactory.testVehicle(1, oldCapacity);
        PlanningVehicle changeVehicle = PlanningVehicleFactory.testVehicle(2, newCapacity);

        mockSolver.whenLookingUp(changeVehicle).thenReturn(workingVehicle);

        // do change
        mockSolver.addProblemChange(new ChangeVehicleCapacity(changeVehicle));

        assertThat(workingVehicle.getCapacity()).isEqualTo(newCapacity);
        mockSolver.verifyProblemPropertyChanged(changeVehicle);
    }
}
