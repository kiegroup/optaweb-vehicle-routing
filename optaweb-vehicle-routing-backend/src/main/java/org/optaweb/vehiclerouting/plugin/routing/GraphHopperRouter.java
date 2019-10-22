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

package org.optaweb.vehiclerouting.plugin.routing;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.reader.osm.GraphHopperOSM;
import com.graphhopper.util.PointList;
import com.graphhopper.util.shapes.BBox;
import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.service.distance.DistanceCalculator;
import org.optaweb.vehiclerouting.service.region.BoundingBox;
import org.optaweb.vehiclerouting.service.region.Region;
import org.optaweb.vehiclerouting.service.route.Router;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Provides geographical information needed for route optimization.
 */
@Component
@ConditionalOnProperty(prefix = "app.routing", name = "engine", havingValue = "graphhopper", matchIfMissing = true)
class GraphHopperRouter implements Router, DistanceCalculator, Region {

    private final GraphHopperOSM graphHopper;

    @Autowired
    GraphHopperRouter(GraphHopperOSM graphHopper) {
        this.graphHopper = graphHopper;
    }

    @Override
    public List<Coordinates> getPath(Coordinates from, Coordinates to) {
        GHRequest ghRequest = new GHRequest(
                from.latitude().doubleValue(),
                from.longitude().doubleValue(),
                to.latitude().doubleValue(),
                to.longitude().doubleValue());
        PointList points = graphHopper.route(ghRequest).getBest().getPoints();
        return StreamSupport.stream(points.spliterator(), false)
                .map(ghPoint3D -> Coordinates.valueOf(ghPoint3D.lat, ghPoint3D.lon))
                .collect(Collectors.toList());
    }

    @Override
    public long travelTimeMillis(Coordinates from, Coordinates to) {
        GHRequest ghRequest = new GHRequest(
                from.latitude().doubleValue(),
                from.longitude().doubleValue(),
                to.latitude().doubleValue(),
                to.longitude().doubleValue());
        GHResponse ghResponse = graphHopper.route(ghRequest);
        // TODO return wrapper that can hold both the result and error explanation instead of throwing exception
        if (ghResponse.hasErrors()) {
            throw new RuntimeException("No route", ghResponse.getErrors().get(0));
        }
        return ghResponse.getBest().getTime();
    }

    @Override
    public BoundingBox getBounds() {
        BBox bounds = graphHopper.getGraphHopperStorage().getBounds();
        return new BoundingBox(
                Coordinates.valueOf(bounds.minLat, bounds.minLon),
                Coordinates.valueOf(bounds.maxLat, bounds.maxLon)
        );
    }
}
