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

import { ActionFactory } from '../types';
import {
  ActionType,
  AddLocationAction,
  AddVehicleAction,
  ClearRouteAction,
  DeleteLocationAction,
  DeleteVehicleAction,
  LatLngWithDescription,
  RoutingPlan,
  UpdateRouteAction,
} from './types';

export const addVehicle: ActionFactory<void, AddVehicleAction> = () => ({
  type: ActionType.ADD_VEHICLE,
});

export const deleteVehicle: ActionFactory<number, DeleteVehicleAction> = id => ({
  type: ActionType.DELETE_VEHICLE,
  value: id,
});

export const addLocation: ActionFactory<LatLngWithDescription, AddLocationAction> = location => ({
  type: ActionType.ADD_LOCATION,
  value: location,
});

export const deleteLocation: ActionFactory<number, DeleteLocationAction> = id => ({
  type: ActionType.DELETE_LOCATION,
  value: id,
});

export const clearRoute: ActionFactory<void, ClearRouteAction> = () => ({
  type: ActionType.CLEAR_SOLUTION,
});

export const updateRoute: ActionFactory<RoutingPlan, UpdateRouteAction> = plan => ({
  plan,
  type: ActionType.UPDATE_ROUTING_PLAN,
});
