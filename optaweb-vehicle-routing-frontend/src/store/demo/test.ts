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

import { mockStore } from '../mockStore';
import { AppState } from '../types';
import { WebSocketConnectionStatus } from '../websocket/types';
import * as actions from './actions';
import reducer, { demoOperations } from './index';
import { Demo } from './types';

describe('Demo operations', () => {
  it('demo request should call loadDemo() on client', () => {
    const { store, client } = mockStore(state);

    // verify requestDemo operation calls the client
    store.dispatch(demoOperations.requestDemo());
    expect(client.loadDemo).toHaveBeenCalledTimes(1);

    expect(store.getActions()).toEqual([actions.requestDemo()]);
  });
});

describe('Demo reducers', () => {
  it('request demo', () => {
    const initialState: Demo = { isLoading: false, demoSize: 798 };
    const expectedState: Demo = { isLoading: true, demoSize: -1 };
    expect(
      reducer(initialState, actions.requestDemo()),
    ).toEqual(expectedState);
  });
  it('start loading when loading requested', () => {
    const demoSize: number = 5;
    const initialState: Demo = { isLoading: true, demoSize: -1 };
    const expectedState: Demo = { isLoading: true, demoSize };
    expect(
      reducer(initialState, actions.startLoading(demoSize)),
    ).toEqual(expectedState);
  });
  it('start loading when loading requested by someone else', () => {
    const demoSize: number = 5;
    const initialState: Demo = { isLoading: false, demoSize: -1 };
    const expectedState: Demo = { isLoading: true, demoSize };
    expect(
      reducer(initialState, actions.startLoading(demoSize)),
    ).toEqual(expectedState);
  });
  it('loading flag should be cleared when demo is loaded', () => {
    const demoSize: number = 5;
    const initialState: Demo = { isLoading: true, demoSize };
    const expectedState: Demo = { isLoading: false, demoSize };
    expect(
      reducer(initialState, actions.finishLoading()),
    ).toEqual(expectedState);
  });
});

const state: AppState = {
  connectionStatus: WebSocketConnectionStatus.CLOSED,
  serverInfo: {
    boundingBox: null,
    countryCodes: [],
  },
  demo: {
    demoSize: 0,
    isLoading: false,
  },
  plan: {
    distance: '10',
    depot: null,
    routes: [{
      visits: [{
        id: 1,
        lat: 1.345678,
        lng: 1.345678,
      }, {
        id: 2,
        lat: 2.345678,
        lng: 2.345678,
      }, {
        id: 3,
        lat: 3.676111,
        lng: 3.568333,
      }],

      track: [{ lat: 0.111222, lng: 0.222333 }, { lat: 0.444555, lng: 0.555666 }],
    }],
  },
};
