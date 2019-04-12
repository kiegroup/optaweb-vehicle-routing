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

/**
 * ThunkCommand is a ThunkAction that has no result (it's typically something like
 * `Promise<ActionAfterDataFetched>`, but sending messages over WebSocket usually has no response
 * (with the exception of subscribe), so most of our operations are void).
 *
 * @template A Type of action(s) allowed to be dispatched.
 */
import { Action } from 'redux';
import { ThunkAction } from 'redux-thunk';
import WebSocketClient from 'websocket/WebSocketClient';
import { Demo } from './demo/types';
import { RoutingPlan } from './route/types';
import { ServerInfo } from './server/types';
import { WebSocketConnectionStatus } from './websocket/types';

export type ThunkCommand<A extends Action> = ThunkAction<void, AppState, WebSocketClient, A>;

export interface AppState {
  readonly serverInfo: ServerInfo;
  readonly plan: RoutingPlan;
  readonly connectionStatus: WebSocketConnectionStatus;
  readonly demo: Demo;
}
