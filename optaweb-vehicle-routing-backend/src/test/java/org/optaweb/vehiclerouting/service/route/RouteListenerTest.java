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
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.optaweb.vehiclerouting.domain.LatLng;
import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.domain.Route;
import org.optaweb.vehiclerouting.domain.RouteWithTrack;
import org.optaweb.vehiclerouting.domain.RoutingPlan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RouteListenerTest {

    @Mock
    private Router router;
    @Mock
    private RoutePublisher publisher;
    @Captor
    private ArgumentCaptor<RoutingPlan> routeArgumentCaptor;
    @InjectMocks
    private RouteListener routeListener;

    @Test
    public void new_listener_should_return_empty_best_route() {
        RoutingPlan bestRoutingPlan = routeListener.getBestRoutingPlan();
        assertThat(bestRoutingPlan.distance()).isEqualTo("0");
        assertThat(bestRoutingPlan.depot()).isEmpty();
        assertThat(bestRoutingPlan.routes()).isEmpty();
    }

    @Test
    public void event_with_no_routes_should_be_published_as_an_empty_routing_plan() {
        RouteChangedEvent event = new RouteChangedEvent(this, "", null, Collections.emptyList());
        routeListener.onApplicationEvent(event);
        verifyZeroInteractions(router);
        verify(publisher).publish(routeArgumentCaptor.capture());

        RoutingPlan routingPlan = routeArgumentCaptor.getValue();
        assertThat(routingPlan.depot()).isEmpty();
        assertThat(routingPlan.routes()).isEmpty();
    }

    @Test
    public void event_with_no_visits_and_a_depot_should_be_published_as_plan_with_empty_routes() {
        final LatLng depotLatLng = LatLng.valueOf(0.0, 0.1);
        final Location depot = new Location(1, depotLatLng);
        Route route = new Route(depot, Collections.emptyList());

        RouteChangedEvent event = new RouteChangedEvent(this, "0 km", depot, Collections.singletonList(route));
        routeListener.onApplicationEvent(event);

        verifyZeroInteractions(router);
        verify(publisher).publish(routeArgumentCaptor.capture());

        RoutingPlan routingPlan = routeArgumentCaptor.getValue();
        assertThat(routingPlan.depot()).contains(depot);
        assertThat(routingPlan.routes()).hasSize(1);
        RouteWithTrack routeWithTrack = routingPlan.routes().iterator().next();
        assertThat(routeWithTrack.depot()).isEqualTo(depot);
        assertThat(routeWithTrack.visits()).isEmpty();
        assertThat(routeWithTrack.track()).isEmpty();
    }

    @Test
    public void listener_should_publish_routing_plan_when_an_update_event_occurs() {
        final LatLng depotLatLng = LatLng.valueOf(0.0, 0.1);
        final LatLng visitLatLng = LatLng.valueOf(2.0, -0.2);
        final LatLng checkpoint1 = LatLng.valueOf(12, 12);
        final LatLng checkpoint2 = LatLng.valueOf(21, 21);
        List<LatLng> path1 = Arrays.asList(depotLatLng, checkpoint1, checkpoint2, visitLatLng);
        List<LatLng> path2 = Arrays.asList(visitLatLng, checkpoint2, checkpoint1, depotLatLng);
        when(router.getPath(depotLatLng, visitLatLng)).thenReturn(path1);
        when(router.getPath(visitLatLng, depotLatLng)).thenReturn(path2);

        final Location depot = new Location(1, depotLatLng);
        final Location visit = new Location(2, visitLatLng);
        final String distance = "xy";

        Route route = new Route(depot, Collections.singletonList(visit));
        RouteChangedEvent event = new RouteChangedEvent(this, distance, depot, Collections.singletonList(route));

        routeListener.onApplicationEvent(event);
        verify(publisher).publish(routeArgumentCaptor.capture());

        RoutingPlan routingPlan = routeArgumentCaptor.getValue();
        assertThat(routingPlan.distance()).isEqualTo(distance);
        assertThat(routingPlan.depot()).contains(depot);
        assertThat(routingPlan.routes()).hasSize(1);
        RouteWithTrack routeWithTrack = routingPlan.routes().iterator().next();
        assertThat(routeWithTrack.depot()).isEqualTo(depot);
        assertThat(routeWithTrack.visits()).containsExactly(visit);
        assertThat(routeWithTrack.track()).containsExactly(path1, path2);

        assertThat(routeListener.getBestRoutingPlan()).isEqualTo(routingPlan);
    }
}
