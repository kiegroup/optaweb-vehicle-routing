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

import { ILatLng, ITSPRouteWithSegments } from './types';

export enum ActionType {
  SOLUTION_UPDATES_DATA = 'SOLUTION_UPDATES_DATA',
  DELETE_LOCATION = 'DELETE_LOCATION',
  ADD_LOCATION = 'ADD_LOCATION',
  LOAD_DEMO = 'LOAD_DEMO',
  CLEAR_SOLUTION = 'CLEAR_SOLUTION',
}

export interface IAddLocationAction {
  readonly type: ActionType.ADD_LOCATION;
  readonly value: ILatLng;
}

export interface IClearSolutionAction {
  readonly type: ActionType.CLEAR_SOLUTION;
}

export interface IDeleteLocationAction {
  readonly type: ActionType.DELETE_LOCATION;
  readonly value: number;
}

export interface ILoadDemoAction {
  readonly type: ActionType.LOAD_DEMO;
}

export interface IUpdateTSPSolutionAction {
  readonly type: ActionType.SOLUTION_UPDATES_DATA;
  readonly solution: ITSPRouteWithSegments;
}

export type TspAction =
  | IAddLocationAction
  | IClearSolutionAction
  | IDeleteLocationAction
  | IUpdateTSPSolutionAction;

const addLocation = (location: ILatLng): IAddLocationAction => ({
  type: ActionType.ADD_LOCATION,
  value: location,
});

const deleteLocation = (id: number): IDeleteLocationAction => ({
  type: ActionType.DELETE_LOCATION,
  value: id,
});

const loadDemo = (): ILoadDemoAction => ({
  type: ActionType.LOAD_DEMO,
});

const clearSolution = (): IClearSolutionAction => ({
  type: ActionType.CLEAR_SOLUTION,
});

const updateTSPSolution = (solution: ITSPRouteWithSegments): IUpdateTSPSolutionAction => ({
  solution,
  type: ActionType.SOLUTION_UPDATES_DATA,
});

export default {
  addLocation,
  clearSolution,
  deleteLocation,
  loadDemo,
  updateTSPSolution,
};
