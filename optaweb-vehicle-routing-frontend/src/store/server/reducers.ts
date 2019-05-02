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

import { ActionType, ServerInfo, ServerInfoAction } from './types';

export const initialServerState: ServerInfo = {
  boundingBox: null,
  countryCodes: [],
  demos: [],
};

const routeReducer = (state = initialServerState, action: ServerInfoAction): ServerInfo => {
  switch (action.type) {
    case ActionType.SERVER_INFO: {
      return action.value;
    }
    default:
      return state;
  }
};

export default routeReducer;
