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
import actions, {
  IAddLocationAction,
  IClearSolutionAction,
  IDeleteLocationAction,
  ILoadDemoAction,
} from './actions';
import { ILatLng } from './types';

const addLocationOp: ActionCreator<ThunkCommand<IAddLocationAction>> = (
  location: ILatLng,
) => (
  dispatch, state, client,
) => {
  dispatch(actions.addLocation(location));
  client.addLocation(location);
};

const loadDemoOp: ActionCreator<ThunkCommand<ILoadDemoAction>> = () => (
  dispatch, state, client,
) => {
  dispatch(actions.loadDemo());
  client.loadDemo();
};

const deleteLocationOp: ActionCreator<ThunkCommand<IDeleteLocationAction>> = (
  locationId: number,
) => (
  dispatch, state, client,
) => {
  dispatch(actions.deleteLocation(locationId));
  client.deleteLocation(locationId);
};

const clearSolutionOp: ActionCreator<ThunkCommand<IClearSolutionAction>> = () => (
  dispatch, state, client,
) => {
  dispatch(actions.clearSolution());
  client.clear();
};

export default {
  addLocation: addLocationOp,
  clearSolution: clearSolutionOp,
  deleteLocation: deleteLocationOp,
  loadDemo: loadDemoOp,
};
