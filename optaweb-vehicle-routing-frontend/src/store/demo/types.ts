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

import { Action } from 'redux';

export enum ActionType {
  REQUEST_DEMO = 'REQUEST_DEMO',
  START_LOADING = 'START_LOADING',
  FINISH_LOADING = 'FINISH_LOADING',
}

export interface RequestDemoAction extends Action<ActionType.REQUEST_DEMO> {
}

export interface StartLoadingAction extends Action<ActionType.START_LOADING> {
  readonly size: number;
}

export interface FinishLoadingAction extends Action<ActionType.FINISH_LOADING> {
}

export type DemoAction =
  | RequestDemoAction
  | StartLoadingAction
  | FinishLoadingAction;

export interface Demo {
  readonly isLoading: boolean;
  readonly demoSize: number;
}
