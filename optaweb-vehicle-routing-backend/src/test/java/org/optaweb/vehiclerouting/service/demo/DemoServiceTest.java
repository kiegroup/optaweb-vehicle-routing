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

package org.optaweb.vehiclerouting.service.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.domain.RoutingProblem;
import org.optaweb.vehiclerouting.domain.Vehicle;
import org.optaweb.vehiclerouting.domain.VehicleData;
import org.optaweb.vehiclerouting.domain.VehicleFactory;
import org.optaweb.vehiclerouting.service.demo.dataset.DataSetMarshaller;
import org.optaweb.vehiclerouting.service.location.LocationRepository;
import org.optaweb.vehiclerouting.service.location.LocationService;
import org.optaweb.vehiclerouting.service.vehicle.VehicleRepository;
import org.optaweb.vehiclerouting.service.vehicle.VehicleService;

@ExtendWith(MockitoExtension.class)
class DemoServiceTest {

    @Mock
    private RoutingProblemList routingProblems;
    @Mock
    private LocationService locationService;
    @Mock
    private LocationRepository locationRepository;
    @Mock
    private VehicleService vehicleService;
    @Mock
    private VehicleRepository vehicleRepository;
    @Mock
    private DataSetMarshaller dataSetMarshaller;
    @InjectMocks
    private DemoService demoService;

    @Captor
    private ArgumentCaptor<RoutingProblem> routingProblemCaptor;

    private final String problemName = "Testing problem";
    private final List<VehicleData> vehicles = Arrays.asList(
            VehicleFactory.vehicleData("v1", 10),
            VehicleFactory.vehicleData("v2", 10));
    private final Location depot = new Location(1, Coordinates.valueOf(1.0, 7), "Depot");
    private final List<Location> visits = Arrays.asList(new Location(2, Coordinates.valueOf(2.0, 9), "Visit"));
    private final RoutingProblem routingProblem = new RoutingProblem(problemName, vehicles, depot, visits);

    @Test
    void demos_should_return_routing_problems() {
        // arrange
        when(routingProblems.all()).thenReturn(Arrays.asList(routingProblem));
        // act
        Collection<RoutingProblem> problems = demoService.demos();
        // assert
        assertThat(problems).containsExactly(routingProblem);
    }

    @Test
    void loadDemo() {
        // arrange
        Location location = new Location(10, Coordinates.valueOf(1, 2));
        when(routingProblems.byName(problemName)).thenReturn(routingProblem);
        when(locationService.createLocation(any(Coordinates.class), anyString())).thenReturn(Optional.of(location));
        // act
        demoService.loadDemo(problemName);
        // assert
        verify(locationService, times(routingProblem.visits().size() + 1))
                .createLocation(any(Coordinates.class), anyString());
        verify(vehicleService, times(routingProblem.vehicles().size()))
                .createVehicle(any(VehicleData.class));
    }

    @Test
    void retry_when_adding_location_fails() {
        when(routingProblems.byName(problemName)).thenReturn(routingProblem);
        when(locationService.createLocation(any(Coordinates.class), anyString())).thenReturn(Optional.empty());
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> demoService.loadDemo(problemName))
                .withMessageContaining(depot.coordinates().toString());
        verify(locationService, times(DemoService.MAX_TRIES)).createLocation(any(Coordinates.class), anyString());
    }

    @Test
    void export_should_marshal_routing_plans_with_locations_and_vehicles_from_repository() {
        Location depot = new Location(0, Coordinates.valueOf(1.0, 2.0), "Depot");
        Location visit1 = new Location(1, Coordinates.valueOf(11.0, 22.0), "Visit 1");
        Location visit2 = new Location(2, Coordinates.valueOf(22.0, 33.0), "Visit 2");
        Vehicle vehicle1 = VehicleFactory.createVehicle(11, "Vehicle 1", 100);
        Vehicle vehicle2 = VehicleFactory.createVehicle(12, "Vehicle 2", 200);
        when(locationRepository.locations()).thenReturn(Arrays.asList(depot, visit1, visit2));
        when(vehicleRepository.vehicles()).thenReturn(Arrays.asList(vehicle1, vehicle2));

        demoService.exportDataSet();

        RoutingProblem routingProblem = verifyAndCaptureMarshalledProblem();
        assertThat(routingProblem.name()).isNotNull();
        assertThat(routingProblem.depot()).contains(depot);
        assertThat(routingProblem.visits()).containsExactly(visit1, visit2);
        assertThat(routingProblem.vehicles()).containsExactly(vehicle1, vehicle2);
    }

    @Test
    void export_should_marshal_empty_routing_plan_when_repositories_empty() {
        when(locationRepository.locations()).thenReturn(Collections.emptyList());
        when(vehicleRepository.vehicles()).thenReturn(Collections.emptyList());

        demoService.exportDataSet();

        RoutingProblem routingProblem = verifyAndCaptureMarshalledProblem();
        assertThat(routingProblem.name()).isNotNull();
        assertThat(routingProblem.depot()).isEmpty();
        assertThat(routingProblem.visits()).isEmpty();
        assertThat(routingProblem.vehicles()).isEmpty();
    }

    private RoutingProblem verifyAndCaptureMarshalledProblem() {
        verify(dataSetMarshaller).marshal(routingProblemCaptor.capture());
        return routingProblemCaptor.getValue();
    }
}
