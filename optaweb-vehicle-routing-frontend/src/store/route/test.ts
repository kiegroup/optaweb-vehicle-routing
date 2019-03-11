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

import { Middleware } from 'redux';
import createMockStore, { MockStoreCreator, MockStoreEnhanced } from 'redux-mock-store';
import thunk, { ThunkDispatch } from 'redux-thunk';
import WebSocketClient from 'websocket/WebSocketClient';
import { IAppState } from '../configStore';
import { WebSocketAction, WebSocketConnectionStatus } from '../websocket/types';
import * as actions from './actions';
import reducer, { routeOperations, routeSelectors } from './index';
import { initialRouteState } from './reducers';
import { ILatLng, IUpdateRouteAction } from './types';

jest.mock('websocket/WebSocketClient');

describe('Route operations', () => {
  it('should dispatch actions and call client', () => {
    const client = new WebSocketClient('');

    // mock store
    const middlewares: Middleware[] = [thunk.withExtraArgument(client)];
    type DispatchExts = ThunkDispatch<IAppState, WebSocketClient,
      WebSocketAction | IUpdateRouteAction>;
    const mockStoreCreator: MockStoreCreator<IAppState, DispatchExts> =
      createMockStore<IAppState, DispatchExts>(middlewares);
    const store: MockStoreEnhanced<IAppState, DispatchExts> = mockStoreCreator(state);

    store.dispatch(routeOperations.clearRoute());
    expect(store.getActions()).toEqual([actions.clearRoute()]);
    expect(client.clear).toHaveBeenCalledTimes(1);

    store.clearActions();

    const id = 3214;
    store.dispatch(routeOperations.deleteLocation(id));
    expect(store.getActions()).toEqual([actions.deleteLocation(id)]);
    expect(client.deleteLocation).toHaveBeenCalledTimes(1);
    expect(client.deleteLocation).toHaveBeenCalledWith(id);

    store.clearActions();

    const latLng: ILatLng = state.route.segments[0];
    store.dispatch(routeOperations.addLocation(latLng));
    expect(store.getActions()).toEqual([actions.addLocation(latLng)]);
    expect(client.addLocation).toHaveBeenCalledTimes(1);
    expect(client.addLocation).toHaveBeenCalledWith(latLng);
  });
});

describe('Route reducers', () => {
  it('clear route', () => {
    expect(
      reducer(state.route, actions.clearRoute()),
    ).toEqual(state.route);
  });
  it('add location', () => {
    expect(
      reducer(state.route, actions.addLocation(state.route.segments[2])),
    ).toEqual(state.route);
  });
  it('delete location', () => {
    expect(
      reducer(state.route, actions.deleteLocation(1)),
    ).toEqual(state.route);
  });
  it('update route', () => {
    expect(
      reducer(initialRouteState, actions.updateRoute(state.route)),
    ).toEqual(state.route);
  });
});

describe('Route selectors', () => {
  it('domicile should be the first location ID', () => {
    expect(
      routeSelectors.getDomicileId(state.route),
    ).toEqual(1);
  });
  it('domicile should not be a positive number if route is empty', () => {
    expect(
      routeSelectors.getDomicileId(initialRouteState),
    ).not.toBeGreaterThanOrEqual(0);
  });
});

const state: IAppState = {
  connectionStatus: WebSocketConnectionStatus.CLOSED,
  demo: {
    demoSize: 0,
    isLoading: false,
  },
  route: {
    distance: '10',
    locations: [
      {
        id: 1,
        lat: 1.345678,
        lng: 1.345678,
      },
      {
        id: 2,
        lat: 2.345678,
        lng: 2.345678,
      },
      {
        id: 3,
        lat: 3.676111,
        lng: 3.568333,
      },
    ],
    segments: [{ lat: 0.111222, lng: 0.222333 }, { lat: 0.444555, lng: 0.555666 }],
  },
};
