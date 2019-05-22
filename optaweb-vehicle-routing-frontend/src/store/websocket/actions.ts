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

import { ActionFactory } from '../types';
import { ActionType, InitWsConnectionAction, WsConnectionFailureAction, WsConnectionSuccessAction } from './types';

export const initWsConnection: ActionFactory<void, InitWsConnectionAction> = () => ({
  type: ActionType.WS_CONNECT,
});

export const wsConnectionSuccess: ActionFactory<void, WsConnectionSuccessAction> = () => ({
  type: ActionType.WS_CONNECT_SUCCESS,
});

export const wsConnectionFailure: ActionFactory<string, WsConnectionFailureAction> = error => ({
  type: ActionType.WS_CONNECT_FAILURE,
  error,
});
