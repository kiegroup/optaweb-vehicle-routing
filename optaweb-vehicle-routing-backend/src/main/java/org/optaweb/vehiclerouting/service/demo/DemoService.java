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

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.optaweb.vehiclerouting.domain.LatLng;
import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.domain.RoutingProblem;
import org.optaweb.vehiclerouting.service.demo.dataset.DataSetMarshaller;
import org.optaweb.vehiclerouting.service.location.LocationRepository;
import org.optaweb.vehiclerouting.service.location.LocationService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Performs demo-related use cases.
 */
@Service
public class DemoService {

    static final int MAX_TRIES = 10;

    private final DemoProperties properties;
    private final LocationService locationService;
    private final DataSetMarshaller dataSetMarshaller;
    private final LocationRepository locationRepository;

    public DemoService(
            DemoProperties properties,
            LocationService locationService,
            DataSetMarshaller dataSetMarshaller,
            LocationRepository locationRepository) {
        this.properties = properties;
        this.locationService = locationService;
        this.dataSetMarshaller = dataSetMarshaller;
        this.locationRepository = locationRepository;
    }

    @Async
    public void loadDemo() {
        RoutingProblem routingProblem = dataSetMarshaller.unmarshall(belgiumReader());

        // Add depot
        addWithRetry(routingProblem.getDepot().getLatLng(), routingProblem.getDepot().getDescription());

        for (int i = 0; i < getDemoSize() - 1; i++) {
            // TODO start randomizing only after using all available cities (=> reproducibility for small demos)
            Location visit = routingProblem.getVisits().get(i % routingProblem.getVisits().size());
            addWithRetry(visit.getLatLng(), visit.getDescription());
        }
    }

    private void addWithRetry(LatLng location, String description) {
        int tries = 0;
        while (tries < MAX_TRIES && !locationService.createLocation(randomize(location), description)) {
            tries++;
        }
        if (tries == MAX_TRIES) {
            throw new RuntimeException(
                    "Impossible to create a new location near " + location + " after " + tries + " attempts"
            );
        }
    }

    private LatLng randomize(LatLng latLng) {
        return LatLng.valueOf(
                latLng.getLatitude().doubleValue() + Math.random() * 0.08 - 0.04,
                latLng.getLongitude().doubleValue() + Math.random() * 0.08 - 0.04
        );
    }

    public int getDemoSize() {
        int size = properties.getSize();
        return size >= 0 ? size : dataSetMarshaller.unmarshall(belgiumReader()).getVisits().size() + 1;
    }

    public String exportDataSet() {
        // FIXME still relying on the fact that the first location in the repository is the depot
        List<Location> visits = new ArrayList<>(locationRepository.locations());
        Location depot = visits.remove(0);
        return dataSetMarshaller.marshall(new RoutingProblem("Custom Vehicle Routing instance", depot, visits));
    }

    private Reader belgiumReader() {
        return new InputStreamReader(DemoService.class.getResourceAsStream("belgium-cities.yaml"));
    }
}
