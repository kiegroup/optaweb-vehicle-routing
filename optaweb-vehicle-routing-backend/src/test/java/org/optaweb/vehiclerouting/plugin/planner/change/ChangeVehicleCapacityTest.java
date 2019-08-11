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

package org.optaweb.vehiclerouting.plugin.planner.change;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.examples.vehiclerouting.domain.Depot;
import org.optaplanner.examples.vehiclerouting.domain.Vehicle;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.domain.location.Location;
import org.optaplanner.examples.vehiclerouting.domain.location.RoadLocation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChangeVehicleCapacityTest {

    @Mock
    private ScoreDirector<VehicleRoutingSolution> scoreDirector;

    @Test
    void change_vehicle_capacity() {
        Location location = new RoadLocation(1, 2.0, 3.0);
        Depot depot = new Depot();
        depot.setLocation(location);

        int oldCapacity = 100;
        int newCapacity = 50;

        Vehicle workingVehicle = new Vehicle();
        workingVehicle.setId(1L);
        workingVehicle.setDepot(depot);
        workingVehicle.setCapacity(oldCapacity);
        Vehicle changeVehicle = new Vehicle();
        changeVehicle.setId(1L);
        changeVehicle.setDepot(depot);
        changeVehicle.setCapacity(newCapacity);

        when(scoreDirector.lookUpWorkingObject(changeVehicle)).thenReturn(workingVehicle);

        // do change
        ChangeVehicleCapacity changeVehicleCapacity = new ChangeVehicleCapacity(changeVehicle);
        changeVehicleCapacity.doChange(scoreDirector);

        assertThat(workingVehicle.getCapacity()).isEqualTo(newCapacity);

        verify(scoreDirector).beforeProblemPropertyChanged(workingVehicle);
        verify(scoreDirector).afterProblemPropertyChanged(workingVehicle);
        verify(scoreDirector).triggerVariableListeners();
    }

    @Test
    void fail_fast_if_working_object_is_null() {
        Depot depot = new Depot();
        depot.setLocation(new RoadLocation(4L, 1, 2));
        Vehicle vehicle = new Vehicle();
        vehicle.setId(1L);
        vehicle.setDepot(depot);

        assertThatIllegalStateException()
                .isThrownBy(() -> new ChangeVehicleCapacity(vehicle).doChange(scoreDirector))
                .withMessageContaining("working copy of");
    }

}
