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

import { ActionType, IRouteWithSegments, RouteAction } from './types';

export const initialRouteState: IRouteWithSegments = {
  distance: '0.00',
  locations: [],
  segments: [],
};

export default function routeReducer(
  state = initialRouteState,
  action: RouteAction,
): IRouteWithSegments {
  switch (action.type) {
    case ActionType.SOLUTION_UPDATES_DATA: {
      return action.route;
    }
    case ActionType.DELETE_LOCATION: {
      if (state.locations.length === 1) {
        return { ...initialRouteState };
      }
      return state;
    }
    default:
      return state;
  }
}
