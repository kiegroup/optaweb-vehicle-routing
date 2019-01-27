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

import { ActionType, ITSPRouteWithSegments, TspAction } from './types';

export const initialTspState: ITSPRouteWithSegments = {
  distance: '0.00',
  route: [],
  segments: [],
};

export default function tspReducer(
  state = initialTspState,
  action: TspAction,
): ITSPRouteWithSegments {
  switch (action.type) {
    case ActionType.SOLUTION_UPDATES_DATA: {
      return action.solution;
    }
    case ActionType.DELETE_LOCATION: {
      if (state.route.length === 1) {
        return { ...initialTspState };
      }
      return state;
    }
    default:
      return state;
  }
}
