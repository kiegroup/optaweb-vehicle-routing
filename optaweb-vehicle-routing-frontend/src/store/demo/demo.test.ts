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
import { Vehicle } from '../route/types';
import { AppState } from '../types';
import { WebSocketConnectionStatus } from '../websocket/types';
import * as actions from './actions';
import reducer, { demoOperations } from './index';
import { Demo } from './types';

describe('Demo operations', () => {
  it('demo request should call loadDemo() on client', () => {
    const { store, client } = mockStore(state);
    const demoName = 'demo name';

    // verify requestDemo operation calls the client
    store.dispatch(demoOperations.requestDemo(demoName));
    expect(client.loadDemo).toHaveBeenCalledTimes(1);

    expect(store.getActions()).toEqual([actions.requestDemo(demoName)]);
  });
});

describe('Demo reducers', () => {
  it('request demo', () => {
    const demoName = 'some name';
    const initialState: Demo = { isLoading: false, demoName: null };
    const expectedState: Demo = { isLoading: true, demoName };
    expect(
      reducer(initialState, actions.requestDemo(demoName)),
    ).toEqual(expectedState);
  });

  it('start loading when loading requested by someone else', () => {
    const demoName = 'some name';
    const initialState: Demo = { isLoading: false, demoName: null };
    const expectedState: Demo = { isLoading: true, demoName };
    expect(
      reducer(initialState, actions.requestDemo(demoName)),
    ).toEqual(expectedState);
  });

  it('loading flag should be cleared when demo is loaded', () => {
    const demoName = 'some name';
    const initialState: Demo = { isLoading: true, demoName };
    const expectedState: Demo = { isLoading: false, demoName };
    expect(
      reducer(initialState, actions.finishLoading()),
    ).toEqual(expectedState);
  });
});

const vehicle1: Vehicle = { id: 1, name: 'v1', capacity: 5 };
const visit1 = {
  id: 1,
  lat: 1.345678,
  lng: 1.345678,
};
const visit2 = {
  id: 2,
  lat: 2.345678,
  lng: 2.345678,
};
const visit3 = {
  id: 3,
  lat: 3.676111,
  lng: 3.568333,
};

const state: AppState = {
  connectionStatus: WebSocketConnectionStatus.CLOSED,
  serverInfo: {
    boundingBox: null,
    countryCodes: [],
    demos: [],
  },
  userViewport: {
    isDirty: false,
    zoom: 1,
    center: [0, 0],
  },
  demo: {
    demoName: null,
    isLoading: false,
  },
  plan: {
    distance: '10',
    vehicles: [vehicle1],
    depot: null,
    visits: [visit1, visit2, visit3],
    routes: [{
      vehicle: vehicle1,
      visits: [visit1, visit2, visit3],

      track: [[0.111222, 0.222333], [0.444555, 0.555666]],
    }],
  },
};
