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

package org.optaweb.vehiclerouting.plugin.routing;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.service.distance.DistanceCalculator;
import org.optaweb.vehiclerouting.service.region.BoundingBox;
import org.optaweb.vehiclerouting.service.region.Region;
import org.optaweb.vehiclerouting.service.route.Router;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "app.routing", name = "engine", havingValue = "air")
public class AirDistanceRouter implements Router, DistanceCalculator, Region {

    protected static final int TRAVEL_SPEED_KPH = 60;
    // Approximate Metric Equivalents for Degrees. At the equator for longitude and for latitude anywhere,
    // the following approximations are valid: 1° = 111 km (or 60 nautical miles) 0.1° = 11.1 km.
    protected static final double KILOMETERS_PER_DEGREE = 111;
    protected static final long MILLIS_IN_ONE_HOUR = TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS);

    @Override
    public long travelTimeMillis(Coordinates from, Coordinates to) {
        BigDecimal latDiff = to.latitude().subtract(from.latitude());
        BigDecimal lngDiff = to.longitude().subtract(from.longitude());
        double distanceKilometers = Math.sqrt(latDiff.pow(2).add(lngDiff.pow(2)).doubleValue()) * KILOMETERS_PER_DEGREE;
        return (long) Math.floor(distanceKilometers / TRAVEL_SPEED_KPH * MILLIS_IN_ONE_HOUR);
    }

    @Override
    public List<Coordinates> getPath(Coordinates from, Coordinates to) {
        return Arrays.asList(from, to);
    }

    @Override
    public BoundingBox getBounds() {
        return new BoundingBox(Coordinates.valueOf(-90, -180), Coordinates.valueOf(90, 180));
    }
}
