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

import { Client, Frame } from 'webstomp-client';
import {
  ADD_LOCATION,
  CLEAR_SOLUTION,
  DELETE_LOCATION,
  ILatLng,
  ITSPRouteWithSegments,
  SOLUTION_UPDATES_DATA,
  WS_CONNECT,
  WS_CONNECT_FAILURE,
  WS_CONNECT_SUCCESS,
} from './types';

export interface IAddLocationAction {
  readonly type: typeof ADD_LOCATION;
  value?: ILatLng;
}

export interface IClearSolutionAction {
  readonly type: typeof CLEAR_SOLUTION;
}

export interface IDeleteLocationAction {
  readonly type: typeof DELETE_LOCATION;
  value: number;
}

export interface IUpdateTSPSolutionAction {
  readonly type: typeof SOLUTION_UPDATES_DATA;
  solution: ITSPRouteWithSegments;
}

export interface InitWsConnectionAction {
  readonly type: typeof WS_CONNECT;
  value: string;
}

export interface IWsConnectionSuccessAction {
  readonly type: typeof WS_CONNECT_SUCCESS;
  value: Client;
}

export interface IWsConnectionFailureAction {
  readonly type: typeof WS_CONNECT_FAILURE;
  value: Frame | CloseEvent;
}

const addLocation = (location?: ILatLng): IAddLocationAction => ({
  type: ADD_LOCATION,
  value: location,
});

const deleteLocation = (id: number): IDeleteLocationAction => ({
  type: DELETE_LOCATION,
  value: id,
});

const clearSolution = (): IClearSolutionAction => ({
  type: CLEAR_SOLUTION,
});

const updateTSPSolution = (
  solution: ITSPRouteWithSegments,
): IUpdateTSPSolutionAction => ({
  solution,
  type: SOLUTION_UPDATES_DATA,
});

const initWsConnection = (socketUrl: string): InitWsConnectionAction => ({
  type: WS_CONNECT,
  value: socketUrl,
});

const wsConnectionSuccess = (
  webstompSocket: Client,
): IWsConnectionSuccessAction => ({
  type: WS_CONNECT_SUCCESS,
  value: webstompSocket,
});

const wsConnectionFailure = (
  err: Frame | CloseEvent,
): IWsConnectionFailureAction => ({
  type: WS_CONNECT_FAILURE,
  value: err,
});

export default {
  addLocation,
  clearSolution,
  deleteLocation,
  initWsConnection,
  updateTSPSolution,
  wsConnectionFailure,
  wsConnectionSuccess,
};
