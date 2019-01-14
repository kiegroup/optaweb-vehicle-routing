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
import { ILatLng, ITSPRouteWithSegments } from './types';

// *************************************************************************************************
// Action types
// *************************************************************************************************

export enum ActionType {
  SOLUTION_UPDATES_DATA = 'SOLUTION_UPDATES_DATA',
  DELETE_LOCATION = 'DELETE_LOCATION',
  ADD_LOCATION = 'ADD_LOCATION',
  ADD_DEMO_LOCATION = 'ADD_DEMO_LOCATION',
  CLEAR_SOLUTION = 'CLEAR_SOLUTION',
  WS_CONNECT = 'WS_CONNECT',
  WS_CONNECT_SUCCESS = 'WS_CONNECT_SUCCESS',
  WS_CONNECT_FAILURE = 'WS_CONNECT_FAILURE',
}

// *************************************************************************************************
// Action interfaces
// *************************************************************************************************

export interface IAddLocationAction {
  readonly type: ActionType.ADD_LOCATION;
  value?: ILatLng;
}

export interface IClearSolutionAction {
  readonly type: ActionType.CLEAR_SOLUTION;
}

export interface IDeleteLocationAction {
  readonly type: ActionType.DELETE_LOCATION;
  value: number;
}

export interface IUpdateTSPSolutionAction {
  readonly type: ActionType.SOLUTION_UPDATES_DATA;
  solution: ITSPRouteWithSegments;
}

export interface InitWsConnectionAction {
  readonly type: ActionType.WS_CONNECT;
  value: string;
}

export interface IWsConnectionSuccessAction {
  readonly type: ActionType.WS_CONNECT_SUCCESS;
  value: Client;
}

export interface IWsConnectionFailureAction {
  readonly type: ActionType.WS_CONNECT_FAILURE;
  value: Frame | CloseEvent;
}

/**
 * Union type for all actions.
 */
export type TspAction =
  | IAddLocationAction
  | IClearSolutionAction
  | IDeleteLocationAction
  | InitWsConnectionAction
  | IUpdateTSPSolutionAction
  | IWsConnectionFailureAction
  | IWsConnectionSuccessAction;

// *************************************************************************************************
// Action creators
// *************************************************************************************************

// TODO use ActionCreator<IAddLocationAction> for the function interface
const addLocation = (location?: ILatLng): IAddLocationAction => ({
  type: ActionType.ADD_LOCATION,
  value: location,
});

const deleteLocation = (id: number): IDeleteLocationAction => ({
  type: ActionType.DELETE_LOCATION,
  value: id,
});

const clearSolution = (): IClearSolutionAction => ({
  type: ActionType.CLEAR_SOLUTION,
});

const updateTSPSolution = (
  solution: ITSPRouteWithSegments,
): IUpdateTSPSolutionAction => ({
  solution,
  type: ActionType.SOLUTION_UPDATES_DATA,
});

const initWsConnection = (socketUrl: string): InitWsConnectionAction => ({
  type: ActionType.WS_CONNECT,
  value: socketUrl,
});

const wsConnectionSuccess = (
  webstompSocket: Client,
): IWsConnectionSuccessAction => ({
  type: ActionType.WS_CONNECT_SUCCESS,
  value: webstompSocket,
});

const wsConnectionFailure = (
  err: Frame | CloseEvent,
): IWsConnectionFailureAction => ({
  type: ActionType.WS_CONNECT_FAILURE,
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
