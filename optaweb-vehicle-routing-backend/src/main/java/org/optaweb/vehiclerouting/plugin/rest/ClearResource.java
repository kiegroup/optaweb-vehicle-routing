/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.optaweb.vehiclerouting.plugin.rest;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.optaweb.vehiclerouting.service.location.LocationService;
import org.optaweb.vehiclerouting.service.vehicle.VehicleService;

@Path("api/clear")
public class ClearResource {

    private final LocationService locationService;
    private final VehicleService vehicleService;

    @Inject
    public ClearResource(LocationService locationService, VehicleService vehicleService) {
        this.locationService = locationService;
        this.vehicleService = vehicleService;
    }

    @POST
    public void clear() {
        // TODO do this in one step (=> new RoutingPlanService)
        vehicleService.removeAll();
        locationService.removeAll();
    }
}
