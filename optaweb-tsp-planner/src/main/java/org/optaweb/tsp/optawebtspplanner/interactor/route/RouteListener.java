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

package org.optaweb.tsp.optawebtspplanner.interactor.route;

import java.util.ArrayList;
import java.util.List;

import org.optaweb.tsp.optawebtspplanner.core.LatLng;
import org.optaweb.tsp.optawebtspplanner.core.Location;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class RouteListener implements ApplicationListener<RouteChangedEvent> {

    private final Router router;
    private final RoutePublisher publisher;

    private Route bestRoute;

    public RouteListener(Router router, RoutePublisher publisher) {
        this.router = router;
        this.publisher = publisher;
        bestRoute = Route.empty();
    }

    @Override
    public void onApplicationEvent(RouteChangedEvent event) {
        // TODO persist the best solution
        bestRoute = new Route(event.getDistance(), event.getRoute(), segments(event.getRoute()));
        publisher.publish(bestRoute);
    }

    private List<List<LatLng>> segments(List<Location> route) {
        List<List<LatLng>> segments = new ArrayList<>();
        for (int i = 1; i < route.size() + 1; i++) {
            // "trick" to get N -> 0 distance at the end of the loop
            Location fromLocation = route.get(i - 1);
            Location toLocation = route.get(i % route.size());
            List<LatLng> latLngs = router.getRoute(fromLocation.getLatLng(), toLocation.getLatLng());
            segments.add(latLngs);
        }
        return segments;
    }

    public Route getBestRoute() {
        return bestRoute;
    }
}
