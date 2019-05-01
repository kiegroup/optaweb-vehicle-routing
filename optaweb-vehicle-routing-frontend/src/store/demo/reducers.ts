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

import { ActionType, Demo, DemoAction } from './types';

const initialState: Demo = {
  isLoading: false,
  demoName: null,
};

const demoReducer = (state = initialState, action: DemoAction): Demo => {
  switch (action.type) {
    case ActionType.REQUEST_DEMO: {
      return { ...initialState, isLoading: true, demoName: action.name };
    }
    case ActionType.FINISH_LOADING: {
      return { ...state, isLoading: false };
    }
    default:
      return state;
  }
};

export default demoReducer;
