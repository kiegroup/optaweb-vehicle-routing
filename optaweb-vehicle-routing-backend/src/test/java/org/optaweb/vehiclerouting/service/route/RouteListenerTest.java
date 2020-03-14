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

package org.optaweb.vehiclerouting.service.route;

import java.util.Arrays;
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
import org.optaweb.vehiclerouting.domain.RouteWithTrack;
import org.optaweb.vehiclerouting.domain.RoutingPlan;
import org.optaweb.vehiclerouting.domain.Vehicle;
import org.optaweb.vehiclerouting.domain.VehicleFactory;
import org.optaweb.vehiclerouting.service.location.LocationRepository;
import org.optaweb.vehiclerouting.service.vehicle.VehicleRepository;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RouteListenerTest {

    @Mock
    private Router router;
    @Mock
    private RoutePublisher publisher;
    @Mock
    private VehicleRepository vehicleRepository;
    @Mock
    private LocationRepository locationRepository;
    @Captor
    private ArgumentCaptor<RoutingPlan> routeArgumentCaptor;
    @InjectMocks
    private RouteListener routeListener;

    @Test
    void new_listener_should_return_empty_best_route() {
        assertThat(routeListener.getBestRoutingPlan().isEmpty()).isTrue();
    }

    @Test
    void event_with_no_routes_should_be_published_as_an_empty_routing_plan() {
        final long vehicleId = 12;
        final Vehicle vehicle = VehicleFactory.testVehicle(vehicleId);
        when(vehicleRepository.find(vehicleId)).thenReturn(Optional.of(vehicle));
        RouteChangedEvent event = new RouteChangedEvent(
                this,
                "",
                singletonList(vehicleId),
                null,
                emptyList(),
                emptyList()
        );
        routeListener.onApplicationEvent(event);
        verifyNoInteractions(router);

        RoutingPlan routingPlan = verifyAndCapturePublishedPlan();
        assertThat(routingPlan.vehicles()).containsExactly(vehicle);
        assertThat(routingPlan.depot()).isEmpty();
        assertThat(routingPlan.visits()).isEmpty();
        assertThat(routingPlan.routes()).isEmpty();
    }

    @Test
    void event_with_no_visits_and_a_depot_should_be_published_as_plan_with_empty_routes() {
        final Coordinates depotCoordinates = Coordinates.valueOf(0.0, 0.1);
        final Location depot = new Location(1, depotCoordinates);
        final long vehicleId = 448;
        final Vehicle vehicle = VehicleFactory.testVehicle(vehicleId);
        ShallowRoute route = new ShallowRoute(vehicle.id(), depot.id(), emptyList());
        when(vehicleRepository.find(vehicleId)).thenReturn(Optional.of(vehicle));
        when(locationRepository.find(depot.id())).thenReturn(Optional.of(depot));

        RouteChangedEvent event = new RouteChangedEvent(
                this,
                "0 km",
                singletonList(vehicleId),
                depot.id(),
                emptyList(),
                singletonList(route)
        );
        routeListener.onApplicationEvent(event);

        verifyNoInteractions(router);

        RoutingPlan routingPlan = verifyAndCapturePublishedPlan();
        assertThat(routingPlan.vehicles()).containsExactly(vehicle);
        assertThat(routingPlan.depot()).contains(depot);
        assertThat(routingPlan.visits()).isEmpty();
        assertThat(routingPlan.routes()).hasSize(1);
        RouteWithTrack routeWithTrack = routingPlan.routes().iterator().next();
        assertThat(routeWithTrack.depot()).isEqualTo(depot);
        assertThat(routeWithTrack.vehicle()).isEqualTo(vehicle);
        assertThat(routeWithTrack.visits()).isEmpty();
        assertThat(routeWithTrack.track()).isEmpty();
    }

    @Test
    void listener_should_publish_routing_plan_when_an_update_event_occurs() {
        final Coordinates depotCoordinates = Coordinates.valueOf(0.0, 0.1);
        final Coordinates visitCoordinates = Coordinates.valueOf(2.0, -0.2);
        final Coordinates checkpoint1 = Coordinates.valueOf(12, 12);
        final Coordinates checkpoint2 = Coordinates.valueOf(21, 21);
        List<Coordinates> path1 = Arrays.asList(depotCoordinates, checkpoint1, checkpoint2, visitCoordinates);
        List<Coordinates> path2 = Arrays.asList(visitCoordinates, checkpoint2, checkpoint1, depotCoordinates);
        when(router.getPath(depotCoordinates, visitCoordinates)).thenReturn(path1);
        when(router.getPath(visitCoordinates, depotCoordinates)).thenReturn(path2);

        final long vehicleId = -5;
        final Vehicle vehicle = VehicleFactory.testVehicle(vehicleId);
        final Location depot = new Location(1, depotCoordinates);
        final Location visit = new Location(2, visitCoordinates);
        final String distance = "xy";
        when(vehicleRepository.find(vehicleId)).thenReturn(Optional.of(vehicle));
        when(locationRepository.find(depot.id())).thenReturn(Optional.of(depot));
        when(locationRepository.find(visit.id())).thenReturn(Optional.of(visit));

        ShallowRoute route = new ShallowRoute(vehicle.id(), depot.id(), singletonList(visit.id()));
        RouteChangedEvent event = new RouteChangedEvent(
                this,
                distance,
                singletonList(vehicleId),
                depot.id(),
                singletonList(visit.id()),
                singletonList(route)
        );

        routeListener.onApplicationEvent(event);

        RoutingPlan routingPlan = verifyAndCapturePublishedPlan();
        assertThat(routingPlan.distance()).isEqualTo(distance);
        assertThat(routingPlan.vehicles()).containsExactly(vehicle);
        assertThat(routingPlan.depot()).contains(depot);
        assertThat(routingPlan.visits()).containsExactly(visit);
        assertThat(routingPlan.routes()).hasSize(1);
        RouteWithTrack routeWithTrack = routingPlan.routes().iterator().next();
        assertThat(routeWithTrack.vehicle()).isEqualTo(vehicle);
        assertThat(routeWithTrack.depot()).isEqualTo(depot);
        assertThat(routeWithTrack.visits()).containsExactly(visit);
        assertThat(routeWithTrack.track()).containsExactly(path1, path2);

        assertThat(routeListener.getBestRoutingPlan()).isEqualTo(routingPlan);
    }

    @Test
    void should_discard_update_gracefully_if_one_of_the_locations_no_longer_exist() {
        final Vehicle vehicle = VehicleFactory.testVehicle(3);
        final Location depot = new Location(1, Coordinates.valueOf(1.0, 2.0));
        final Location visit = new Location(2, Coordinates.valueOf(-1.0, -2.0));
        when(vehicleRepository.find(vehicle.id())).thenReturn(Optional.of(vehicle));
        when(locationRepository.find(depot.id())).thenReturn(Optional.of(depot));
        when(locationRepository.find(visit.id())).thenReturn(Optional.empty());

        ShallowRoute route = new ShallowRoute(vehicle.id(), depot.id(), singletonList(visit.id()));
        RouteChangedEvent event = new RouteChangedEvent(
                this,
                "",
                singletonList(vehicle.id()),
                depot.id(),
                singletonList(visit.id()),
                singletonList(route)
        );

        // precondition
        assertThat(routeListener.getBestRoutingPlan().isEmpty()).isTrue();

        // must not throw exception
        routeListener.onApplicationEvent(event);

        verify(router, never()).getPath(any(), any());
        verify(publisher, never()).publish(any());

        assertThat(routeListener.getBestRoutingPlan().isEmpty()).isTrue();
    }

    @Test
    void should_discard_update_gracefully_if_one_of_the_vehicles_no_longer_exist() {
        final Vehicle vehicle = VehicleFactory.testVehicle(3);
        final Location depot = new Location(1, Coordinates.valueOf(1.0, 2.0));
        final Location visit = new Location(2, Coordinates.valueOf(-1.0, -2.0));
        when(vehicleRepository.find(vehicle.id())).thenReturn(Optional.empty());
        when(locationRepository.find(depot.id())).thenReturn(Optional.of(depot));

        ShallowRoute route = new ShallowRoute(vehicle.id(), depot.id(), singletonList(visit.id()));
        RouteChangedEvent event = new RouteChangedEvent(
                this,
                "",
                singletonList(vehicle.id()),
                depot.id(),
                singletonList(visit.id()),
                singletonList(route)
        );

        // precondition
        assertThat(routeListener.getBestRoutingPlan().isEmpty()).isTrue();

        // must not throw exception
        routeListener.onApplicationEvent(event);

        verify(router, never()).getPath(any(), any());
        verify(publisher, never()).publish(any());

        assertThat(routeListener.getBestRoutingPlan().isEmpty()).isTrue();
    }

    private RoutingPlan verifyAndCapturePublishedPlan() {
        verify(publisher).publish(routeArgumentCaptor.capture());
        return routeArgumentCaptor.getValue();
    }
}
