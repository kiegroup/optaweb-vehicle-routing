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
  LOAD_DEMO = 'LOAD_DEMO',
  DEMO_LOADED = 'DEMO_LOADED',
}

export interface ILoadDemoAction extends Action<ActionType.LOAD_DEMO> {
  readonly size: number;
}

export interface IDemoLoadingFinishedAction extends Action<ActionType.DEMO_LOADED> {}

export type DemoAction = ILoadDemoAction | IDemoLoadingFinishedAction;

export interface IDemo {
  readonly isLoading: boolean;
  readonly demoSize: number;
}
