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

import types from './types';

const INITIAL_STATE = {
  route: [],
  segments: [],
  domicileId: -1,
  distance: '0.00',
};

export default function tspReducer(state = INITIAL_STATE, action) {
  switch (action.type) {
    case types.SOLUTION_UPDATES_DATA: {
      const { route, segments, distance } = action.solution;
      if (route.length < 1) {
        return state;
      }
      return {
        route,
        segments,
        domicileId: route.length > 0 ? route[0].id : NaN,
        distance,
      };
    }
    case types.DELETE_LOCATION: {
      if (state.route.length === 1) {
        return { ...INITIAL_STATE };
      }
      return state;
    }
    case types.ADD_LOCATION:
    case types.ADD_DEMO_LOCATION:
    default:
      return state;
  }
}
