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

import org.optaweb.vehiclerouting.domain.LatLng;
import org.optaweb.vehiclerouting.service.location.LocationService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Performs demo-related use cases.
 */
@Service
public class DemoService {

    private final DemoProperties properties;
    private final LocationService locationService;

    public DemoService(DemoProperties properties, LocationService locationService) {
        this.properties = properties;
        this.locationService = locationService;
    }

    @Async
    public void loadDemo() {
        for (int i = 0; i < getDemoSize(); i++) {
            // TODO retry if location service fails to create the location
            locationService.createLocation(randomize(Belgium.values()[i % Belgium.values().length]));
        }
    }

    private LatLng randomize(Belgium city) {
        return LatLng.valueOf(
                city.lat + Math.random() * 0.08 - 0.04,
                city.lng + Math.random() * 0.08 - 0.04
        );
    }

    public int getDemoSize() {
        return properties.getSize();
    }
}
