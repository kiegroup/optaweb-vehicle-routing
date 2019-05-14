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

import { ActionCreator } from 'redux';
import { ThunkCommand } from '../types';
import * as actions from './actions';
import { AddLocationAction, ClearRouteAction, DeleteLocationAction, LatLng } from './types';

export const updateRoute = actions.updateRoute;

export const addLocation: (latLng: LatLng, description: string) => ThunkCommand<AddLocationAction> =
  (latLng: LatLng, description: string) =>
    (dispatch, state, client) => {
      dispatch(actions.addLocation(latLng, description));
      client.addLocation({ ...latLng, description });
    };

export const deleteLocation: ActionCreator<ThunkCommand<DeleteLocationAction>> = (locationId: number) =>
  (dispatch, state, client) => {
    dispatch(actions.deleteLocation(locationId));
    client.deleteLocation(locationId);
  };

export const clearRoute: ActionCreator<ThunkCommand<ClearRouteAction>> = () =>
  (dispatch, state, client) => {
    dispatch(actions.clearRoute());
    client.clear();
  };
