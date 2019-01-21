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

import { Action } from 'redux';

export interface ILatLng {
  readonly lat: number;
  readonly lng: number;
}

export interface ILocation extends ILatLng {
  readonly id: number;
}

export interface ITSPRoute {
  readonly route: ILocation[];
  readonly distance: string;
}

export interface ITSPRouteWithSegments extends ITSPRoute {
  readonly segments: ILatLng[];
}

export enum ActionType {
  SOLUTION_UPDATES_DATA = 'SOLUTION_UPDATES_DATA',
  DELETE_LOCATION = 'DELETE_LOCATION',
  ADD_LOCATION = 'ADD_LOCATION',
  CLEAR_SOLUTION = 'CLEAR_SOLUTION',
}

export interface IAddLocationAction extends Action<ActionType.ADD_LOCATION> {
  readonly value: ILatLng;
}

export interface IClearSolutionAction extends Action<ActionType.CLEAR_SOLUTION> {
}

export interface IDeleteLocationAction extends Action<ActionType.DELETE_LOCATION> {
  readonly value: number;
}

export interface IUpdateTSPSolutionAction extends Action<ActionType.SOLUTION_UPDATES_DATA> {
  readonly solution: ITSPRouteWithSegments;
}

export type TspAction =
  | IAddLocationAction
  | IClearSolutionAction
  | IDeleteLocationAction
  | IUpdateTSPSolutionAction;
