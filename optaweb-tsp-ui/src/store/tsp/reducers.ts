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

import { ActionType, TspAction } from './actions';
import { ITSPReducerState, WebSocketConnectionStatus } from './types';

const INITIAL_STATE: ITSPReducerState = {
  distance: '0.00',
  domicileId: -1,
  route: [],
  segments: [],
  ws: WebSocketConnectionStatus.CLOSED,
};

export default function tspReducer(
  state = INITIAL_STATE,
  action: TspAction,
): ITSPReducerState {
  switch (action.type) {
    case ActionType.SOLUTION_UPDATES_DATA: {
      const { route, segments, distance } = action.solution;
      if (route.length === 0 && distance) {
        return { ...INITIAL_STATE };
      }
      return {
        ...state,
        distance,
        domicileId: route.length > 0 ? route[0].id : NaN,
        route,
        segments,
      };
    }
    case ActionType.DELETE_LOCATION: {
      if (state.route.length === 1) {
        return { ...INITIAL_STATE };
      }
      return state;
    }
    case ActionType.WS_CONNECT_SUCCESS: {
      return { ...state, ws: WebSocketConnectionStatus.OPEN };
    }

    case ActionType.WS_CONNECT_FAILURE: {
      return { ...state, ws: WebSocketConnectionStatus.ERROR };
    }
    default:
      return state;
  }
}
