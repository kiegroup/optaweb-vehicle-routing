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

import { resetViewport } from '../client/actions';
import { UserViewport } from '../client/types';
import { demoOperations } from '../demo';
import { mockStore } from '../mockStore';
import { routeOperations } from '../route';
import { RoutingPlan, Vehicle } from '../route/types';
import { serverInfo } from '../server/actions';
import { ServerInfo } from '../server/types';
import { AppState } from '../types';
import * as actions from './actions';
import reducer, { websocketOperations } from './index';
import { WebSocketConnectionStatus } from './types';

jest.useFakeTimers();

const uninitializedCallbackCapture = () => {
  throw new Error('Error callback is uninitialized');
};

const userViewport: UserViewport = {
  isDirty: true,
  zoom: 1,
  center: [0, 0],
};

describe('WebSocket client operations', () => {
  it('should fail connection and reconnect when client crashes', () => {
    const state: AppState = {
      connectionStatus: WebSocketConnectionStatus.CLOSED,
      serverInfo: {
        boundingBox: null,
        countryCodes: [],
        demos: [],
      },
      demo: {
        demoName: null,
        isLoading: false,
      },
      plan: emptyPlan,
      userViewport,
    };

    let errorCallbackCapture: (err: any) => void = uninitializedCallbackCapture;
    let successCallbackCapture: () => void = uninitializedCallbackCapture;
    let subscribeCallbackCapture: (plan: RoutingPlan) => void = uninitializedCallbackCapture;

    const { store, client } = mockStore(state);

    client.connect = jest.fn().mockImplementation((successCallback, errorCallback) => {
      successCallbackCapture = successCallback;
      errorCallbackCapture = errorCallback;
    });

    client.subscribeToRoute = jest.fn().mockImplementation((callback) => {
      subscribeCallbackCapture = callback;
    });

    store.dispatch(websocketOperations.connectClient());
    expect(store.getActions()).toEqual([actions.initWsConnection()]);

    // simulate client disconnection
    const testError = { error: 'TEST_ERROR' };
    errorCallbackCapture(testError);
    expect(store.getActions()).toEqual([
      actions.initWsConnection(),
      actions.wsConnectionFailure(JSON.stringify(testError)),
    ]);

    store.clearActions();

    // verify reconnection has been scheduled
    expect(setTimeout).toHaveBeenCalledTimes(1);
    expect(setTimeout).toHaveBeenLastCalledWith(expect.any(Function), 1000);
    jest.runOnlyPendingTimers();
    expect(store.getActions()).toEqual([actions.initWsConnection()]);

    // pretend client will reconnect successfully on the next attempt
    successCallbackCapture();
    expect(store.getActions()).toEqual([
      actions.initWsConnection(),
      actions.wsConnectionSuccess(),
    ]);
    expect(client.subscribeToRoute).toHaveBeenCalledTimes(1);

    store.clearActions();

    // simulate response to subscription
    subscribeCallbackCapture(emptyPlan);
    expect(store.getActions()).toEqual([routeOperations.updateRoute(emptyPlan)]);
  });

  it('should finish demo loading when all locations are loaded', () => {
    const state: AppState = {
      connectionStatus: WebSocketConnectionStatus.CLOSED,
      serverInfo: {
        boundingBox: null,
        countryCodes: [],
        demos: [{
          name: 'demo',
          visits: nonEmptyPlan.visits.length,
        }],
      },
      demo: {
        demoName: 'demo',
        isLoading: true,
      },
      plan: emptyPlan,
      userViewport,
    };

    const { store, client } = mockStore(state);

    let successCallbackCapture: () => void = uninitializedCallbackCapture;
    client.connect = jest.fn().mockImplementation((successCallback) => {
      successCallbackCapture = successCallback;
    });

    let routeSubscriptionCallback: (plan: RoutingPlan) => void = uninitializedCallbackCapture;
    client.subscribeToRoute = jest.fn().mockImplementation((callback) => {
      routeSubscriptionCallback = callback;
    });

    // connect the client
    store.dispatch(websocketOperations.connectClient());
    expect(store.getActions()).toEqual([actions.initWsConnection()]);

    // simulate successful client connection
    successCallbackCapture();
    expect(store.getActions()).toEqual([
      actions.initWsConnection(),
      actions.wsConnectionSuccess(),
    ]);

    // should be subscribed to all topics
    expect(client.subscribeToRoute).toHaveBeenCalledTimes(1);

    store.clearActions();

    // simulate receiving plan with number of visits matching the expected demo size
    routeSubscriptionCallback(nonEmptyPlan);
    // FINISH_LOADING should be dispatched
    expect(store.getActions()).toEqual([
      routeOperations.updateRoute(nonEmptyPlan),
      demoOperations.finishLoading(),
    ]);
  });

  it('should dispatch server info and reset viewport', () => {
    const state: AppState = {
      connectionStatus: WebSocketConnectionStatus.CLOSED,
      serverInfo: {
        boundingBox: null,
        countryCodes: [],
        demos: [],
      },
      demo: {
        demoName: null,
        isLoading: false,
      },
      plan: emptyPlan,
      userViewport,
    };

    const { store, client } = mockStore(state);

    let successCallbackCapture: () => void = uninitializedCallbackCapture;
    client.connect = jest.fn().mockImplementation((successCallback) => {
      successCallbackCapture = successCallback;
    });

    let serverInfoSubscriptionCallback: (info: ServerInfo) => void = uninitializedCallbackCapture;
    client.subscribeToServerInfo = jest.fn().mockImplementation((callback) => {
      serverInfoSubscriptionCallback = callback;
    });

    // successfully connect the client
    store.dispatch(websocketOperations.connectClient());
    successCallbackCapture();

    // should be subscribed serverInfo topic
    expect(client.subscribeToServerInfo).toHaveBeenCalledTimes(1);

    store.clearActions();

    // when server info arrives
    const info: ServerInfo = {
      boundingBox: null,
      countryCodes: ['AB', 'XY'],
      demos: [{ name: 'Demo name', visits: 20 }],
    };
    serverInfoSubscriptionCallback(info);

    // action should be dispatched
    expect(store.getActions()).toEqual([
      resetViewport(),
      serverInfo(info),
    ]);
  });
});

describe('WebSocket reducers', () => {
  it('connection success should open connection status', () => {
    expect(
      reducer(WebSocketConnectionStatus.CLOSED, actions.wsConnectionSuccess()),
    ).toEqual(WebSocketConnectionStatus.OPEN);
  });

  it('connection failure should fail connection status', () => {
    expect(
      reducer(WebSocketConnectionStatus.OPEN, actions.wsConnectionFailure('test error')),
    ).toEqual(WebSocketConnectionStatus.ERROR);
  });
});

const emptyPlan: RoutingPlan = {
  distance: '',
  vehicles: [],
  depot: null,
  visits: [],
  routes: [],
};

const vehicle1: Vehicle = { id: 1, name: 'v1', capacity: 5 };
const vehicle2: Vehicle = { id: 2, name: 'v2', capacity: 5 };
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
const visit4 = {
  id: 4,
  lat: 4.345678,
  lng: 4.345678,
};
const visit5 = {
  id: 5,
  lat: 5.345678,
  lng: 5.345678,
};
const visit6 = {
  id: 6,
  lat: 6.676111,
  lng: 6.568333,
};
const nonEmptyPlan: RoutingPlan = {
  distance: '1.0',
  vehicles: [
    vehicle1,
    vehicle2,
  ],
  depot: visit1,
  visits: [visit2, visit3, visit4, visit5, visit6],
  routes: [], // not important for the test
};
