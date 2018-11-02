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

function makeActionCreator(type, ...argNames) {
  return function actionCreator(...args) {
    const action = { type };
    argNames.forEach((arg, index) => {
      action[argNames[index]] = args[index];
    });
    return action;
  };
}


// consider https://github.com/infinitered/reduxsauce  for DRY approach

const actions = {
  addLocation: makeActionCreator(types.ADD_LOCATION, 'value'),

  deleteLocation: makeActionCreator(types.DELETE_LOCATION, 'value'),

  updateTSPSolution: makeActionCreator(types.SOLUTION_UPDATES_DATA, 'solution'),


  initWsConnection: makeActionCreator(types.WS_CONNECT, 'value'),
  wsConnectionSuccess: makeActionCreator(types.WS_CONNECT_SUCCESS, 'value'),
  wsConnectionFailure: makeActionCreator(types.WS_CONNECT_FAILURE, 'value'),
};


export default actions;
