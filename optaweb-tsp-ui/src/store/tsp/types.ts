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

export const SOLUTION_UPDATES_DATA = "SOLUTION_UPDATES_DATA";
export const DELETE_LOCATION = "DELETE_LOCATION";
export const ADD_LOCATION = "ADD_LOCATION";
export const ADD_DEMO_LOCATION = "ADD_DEMO_LOCATION";
export const WS_CONNECT = "WS_CONNECT";
export const WS_CONNECT_SUCCESS = "WS_CONNECT_SUCCESS";
export const WS_CONNECT_FAILURE = "WS_CONNECT_FAILURE";

export type SOLUTION_UPDATES_DATA = typeof SOLUTION_UPDATES_DATA;
export type DELETE_LOCATION = typeof DELETE_LOCATION;
export type ADD_LOCATION = typeof ADD_LOCATION;
export type ADD_DEMO_LOCATION = typeof ADD_DEMO_LOCATION;
export type WS_CONNECT = typeof WS_CONNECT;
export type WS_CONNECT_SUCCESS = typeof WS_CONNECT_SUCCESS;
export type WS_CONNECT_FAILURE = typeof WS_CONNECT_FAILURE;

export interface LatLng {
  lat: number;
  lng: number;
}

export interface IdLatLng extends LatLng {
  id: number;
}
export interface TSPRoute {
  route: Array<IdLatLng>;
  domicileId: number;
  distance?: string;
}
