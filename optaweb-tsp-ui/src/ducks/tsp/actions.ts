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

import { GPSLocation, ADD_LOCATION, DELETE_LOCATION, SOLUTION_UPDATES_DATA, WS_CONNECT, WS_CONNECT_SUCCESS, WS_CONNECT_FAILURE, TSPStore } from "./types";
import { Client, Frame } from "webstomp-client";

// Dropped action creator maker due to more complex type handling, a strategy will be defined once needed

export type AddLocationAction = {
  type: typeof ADD_LOCATION;
  value: GPSLocation;
};

export type DeleteLocationAction = {
  type: typeof DELETE_LOCATION;
  value: number;
};

export type UpdateTSPSolutionAction = {
  type: typeof SOLUTION_UPDATES_DATA;
  solution: TSPStore;
};

export type InitWsConnectionAction = {
  type: typeof WS_CONNECT;
  value: string;
};

export type WsConnectionSuccessAction = {
  type: typeof WS_CONNECT_SUCCESS;
  value: Client;
};

export type WsConnectionFailureAction = {
  type: typeof WS_CONNECT_FAILURE;
  value: Frame | CloseEvent;
};

const addLocation = (location: GPSLocation): AddLocationAction => ({
  type: ADD_LOCATION,
  value: location
});

const deleteLocation = (id: number): DeleteLocationAction => ({
  type: DELETE_LOCATION,
  value: id
});

const updateTSPSolution = (
  solution: TSPStore
): UpdateTSPSolutionAction => ({
  type: SOLUTION_UPDATES_DATA,
  solution
});

const initWsConnection = (socketUrl: string): InitWsConnectionAction => ({
  type: WS_CONNECT,
  value: socketUrl
});

const wsConnectionSuccess = (
  webstompSocket: Client
): WsConnectionSuccessAction => ({
  type: WS_CONNECT_SUCCESS,
  value: webstompSocket
});

const wsConnectionFailure = (
  err: Frame | CloseEvent
): WsConnectionFailureAction => ({
  type: WS_CONNECT_FAILURE,
  value: err
});

export default {
  addLocation,
  deleteLocation,
  updateTSPSolution,
  initWsConnection,
  wsConnectionSuccess,
  wsConnectionFailure
};
