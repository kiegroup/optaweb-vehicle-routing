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

import { ThunkCommandFactory } from '../types';
import * as actions from './actions';
import {
  AddLocationAction,
  AddVehicleAction,
  ClearRouteAction,
  DeleteLocationAction,
  DeleteVehicleAction,
  LatLngWithDescription,
  VehicleCapacity,
} from './types';

export const { updateRoute } = actions;

export const addLocation: ThunkCommandFactory<LatLngWithDescription, AddLocationAction> = (
  location => (dispatch, getState, client) => {
    dispatch(actions.addLocation(location));
    client.addLocation(location);
  });

export const deleteLocation: ThunkCommandFactory<number, DeleteLocationAction> = (
  locationId => (dispatch, getState, client) => {
    dispatch(actions.deleteLocation(locationId));
    client.deleteLocation(locationId);
  });

export const addVehicle: ThunkCommandFactory<void, AddVehicleAction> = (
  () => (dispatch, getState, client) => {
    dispatch(actions.addVehicle());
    client.addVehicle();
  });

export const deleteVehicle: ThunkCommandFactory<number, DeleteVehicleAction> = (
  vehicleId => (dispatch, getState, client) => {
    dispatch(actions.deleteVehicle(vehicleId));
    client.deleteVehicle(vehicleId);
  });

export const deleteAnyVehicle: ThunkCommandFactory<void, never> = (
  () => (dispatch, getState, client) => {
    client.deleteAnyVehicle();
  });

export const changeVehicleCapacity: ThunkCommandFactory<VehicleCapacity, never> = (
  ({ vehicleId, capacity }: VehicleCapacity) => (dispatch, getState, client) => {
    client.changeVehicleCapacity(vehicleId, capacity);
  });

export const clearRoute: ThunkCommandFactory<void, ClearRouteAction> = (
  () => (dispatch, getState, client) => {
    dispatch(actions.clearRoute());
    client.clear();
  });
