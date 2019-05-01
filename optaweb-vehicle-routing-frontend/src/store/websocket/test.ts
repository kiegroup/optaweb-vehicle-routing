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

import { demoOperations } from '../demo';
import { mockStore } from '../mockStore';
import { routeOperations } from '../route';
import { RoutingPlan } from '../route/types';
import { serverOperations } from '../server';
import { ServerInfo } from '../server/types';
import { AppState } from '../types';
import * as actions from './actions';
import reducer, { websocketOperations } from './index';
import { WebSocketConnectionStatus } from './types';

jest.useFakeTimers();

const uninitializedCallbackCapture = () => {
  throw new Error('Error callback is uninitialized');
};

describe('WebSocket client operations', () => {
  it('should fail connection and reconnect when client crashes', () => {
    const state: AppState = {
      connectionStatus: WebSocketConnectionStatus.CLOSED,
      serverInfo: {
        boundingBox: null,
        countryCodes: [],
        demo: null,
      },
      demo: {
        demoName: null,
        isLoading: false,
      },
      plan: emptyPlan,
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
    const testError = 'TEST_ERROR';
    errorCallbackCapture(testError);
    expect(store.getActions()).toEqual([
      actions.initWsConnection(),
      actions.wsConnectionFailure(testError),
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
        demo: {
          name: 'demo',
          visits: 5, // equals number of visits in `planWithTwoRoutes`
        },
      },
      demo: {
        demoName: 'demo',
        isLoading: true,
      },
      plan: emptyPlan,
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
    routeSubscriptionCallback(planWithTwoRoutes);
    // FINISH_LOADING should be dispatched
    expect(store.getActions()).toEqual([
      routeOperations.updateRoute(planWithTwoRoutes),
      demoOperations.finishLoading(),
    ]);
  });

  it('should dispatch server info', () => {
    const state: AppState = {
      connectionStatus: WebSocketConnectionStatus.CLOSED,
      serverInfo: {
        boundingBox: null,
        countryCodes: [],
        demo: null,
      },
      demo: {
        demoName: null,
        isLoading: false,
      },
      plan: emptyPlan,
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
    const serverInfo: ServerInfo = {
      boundingBox: null,
      countryCodes: ['AB', 'XY'],
      demo: { name: 'Demo name', visits: 20 },
    };
    serverInfoSubscriptionCallback(serverInfo);

    // action should be dispatched
    expect(store.getActions()).toEqual([serverOperations.serverInfo(serverInfo)]);
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
      reducer(WebSocketConnectionStatus.OPEN, actions.wsConnectionFailure()),
    ).toEqual(WebSocketConnectionStatus.ERROR);
  });
});

const emptyPlan: RoutingPlan = {
  distance: '',
  depot: null,
  routes: [],
};

const planWithTwoRoutes: RoutingPlan = {
  distance: '1.0',
  depot: {
    id: 1,
    lat: 1.345678,
    lng: 1.345678,
  },
  routes: [{
    visits: [{
      id: 2,
      lat: 2.345678,
      lng: 2.345678,
    }, {
      id: 3,
      lat: 3.676111,
      lng: 3.568333,
    }],
    track: [],
  }, {
    visits: [{
      id: 4,
      lat: 1.345678,
      lng: 1.345678,
    }, {
      id: 5,
      lat: 2.345678,
      lng: 2.345678,
    }, {
      id: 6,
      lat: 3.676111,
      lng: 3.568333,
    }],
    track: [],
  }],
};
