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
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.optaweb.vehiclerouting.service.vehicle.VehicleService;

@Path("api/vehicle")
public class VehicleResource {

    private final VehicleService vehicleService;

    @Inject
    public VehicleResource(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @POST
    public void addVehicle() {
        vehicleService.createVehicle();
    }

    /**
     * Delete vehicle.
     *
     * @param id ID of the vehicle to be deleted
     */
    @DELETE
    @Path("{id}")
    public void removeVehicle(@PathParam("id") long id) {
        vehicleService.removeVehicle(id);
    }

    @POST
    @Path("deleteAny")
    public void removeAnyVehicle() {
        vehicleService.removeAnyVehicle();
    }

    @POST
    @Path("{id}/capacity")
    public void changeCapacity(@PathParam("id") long id, int capacity) {
        vehicleService.changeCapacity(id, capacity);
    }
}
