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

import org.optaweb.vehiclerouting.domain.LatLng;
import org.optaweb.vehiclerouting.domain.RoutingProblem;
import org.optaweb.vehiclerouting.service.demo.dataset.DataSetMarshaller;
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

    public DemoService(DemoProperties properties, LocationService locationService, DataSetMarshaller dataSetMarshaller) {
        this.properties = properties;
        this.locationService = locationService;
        this.dataSetMarshaller = dataSetMarshaller;
    }

    @Async
    public void loadDemo() {
        RoutingProblem routingProblem = dataSetMarshaller.unmarshall(belgiumReader());

        // Add depot
        addWithRetry(routingProblem.getDepot());

        for (int i = 0; i < getDemoSize() - 1; i++) {
            // TODO start randomizing only after using all available cities (=> reproducibility for small demos)
            LatLng visit = routingProblem.getVisits().get(i % routingProblem.getVisits().size());
            addWithRetry(visit);
        }
    }

    private void addWithRetry(LatLng location) {
        int tries = 0;
        while (tries < MAX_TRIES && !locationService.createLocation(randomize(location))) {
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
        return dataSetMarshaller.marshall(dataSetMarshaller.unmarshall(belgiumReader()));
    }

    private Reader belgiumReader() {
        return new InputStreamReader(DemoService.class.getResourceAsStream("belgium-cities.yaml"));
    }
}
