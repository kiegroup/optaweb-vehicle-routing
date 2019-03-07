/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.List;

import org.optaweb.vehiclerouting.domain.LatLng;
import org.optaweb.vehiclerouting.domain.Location;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

/**
 * Handles route updates emitted by optimization plugin.
 */
@Service
public class RouteListener implements ApplicationListener<RouteChangedEvent> {

    private final Router router;
    private final RoutePublisher publisher;

    // TODO maybe remove state from the service and get best route from a repository
    private RoutingPlan bestRoutingPlan;

    public RouteListener(Router router, RoutePublisher publisher) {
        this.router = router;
        this.publisher = publisher;
        bestRoutingPlan = RoutingPlan.empty();
    }

    @Override
    public void onApplicationEvent(RouteChangedEvent event) {
        // TODO persist the best solution
        bestRoutingPlan = new RoutingPlan(event.getDistance(), event.getRoute(), paths(event.getRoute()));
        publisher.publish(bestRoutingPlan);
    }

    private List<List<LatLng>> paths(List<Location> route) {
        List<List<LatLng>> paths = new ArrayList<>();
        for (int i = 1; i < route.size() + 1; i++) {
            // "trick" to get N -> 0 distance at the end of the loop
            Location fromLocation = route.get(i - 1);
            Location toLocation = route.get(i % route.size());
            List<LatLng> latLngs = router.getPath(fromLocation.getLatLng(), toLocation.getLatLng());
            paths.add(latLngs);
        }
        return paths;
    }

    public RoutingPlan getBestRoutingPlan() {
        return bestRoutingPlan;
    }
}
