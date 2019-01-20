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
import TspClient from '../../websocket/TspClient';
import { IAppState } from '../configStore';
import { WebSocketAction, WebSocketConnectionStatus } from '../websocket/types';
import * as actions from './actions';
import reducer, { tspOperations, tspSelectors } from './index';
import { initialTspState } from './reducers';
import { ILatLng, IUpdateTSPSolutionAction } from './types';

jest.mock('../../websocket/TspClient');

describe('TSP operations', () => {
  it('should dispatch actions and call client', () => {
    const tspClient = new TspClient('');

    // mock store
    const middlewares: Middleware[] = [thunk.withExtraArgument(tspClient)];
    type DispatchExts = ThunkDispatch<IAppState, TspClient,
      WebSocketAction | IUpdateTSPSolutionAction>;
    const mockStoreCreator: MockStoreCreator<IAppState, DispatchExts> =
      createMockStore<IAppState, DispatchExts>(middlewares);
    const store: MockStoreEnhanced<IAppState, DispatchExts> = mockStoreCreator(state);

    store.dispatch(tspOperations.loadDemo());
    expect(store.getActions()).toEqual([actions.loadDemo()]);
    expect(tspClient.loadDemo).toHaveBeenCalledTimes(1);

    store.clearActions();

    store.dispatch(tspOperations.clearSolution());
    expect(store.getActions()).toEqual([actions.clearSolution()]);
    expect(tspClient.clear).toHaveBeenCalledTimes(1);

    store.clearActions();

    const id = 3214;
    store.dispatch(tspOperations.deleteLocation(id));
    expect(store.getActions()).toEqual([actions.deleteLocation(id)]);
    expect(tspClient.deleteLocation).toHaveBeenCalledTimes(1);
    expect(tspClient.deleteLocation).toHaveBeenCalledWith(id);

    store.clearActions();

    const latLng: ILatLng = state.route[0];
    store.dispatch(tspOperations.addLocation(latLng));
    expect(store.getActions()).toEqual([actions.addLocation(latLng)]);
    expect(tspClient.addLocation).toHaveBeenCalledTimes(1);
    expect(tspClient.addLocation).toHaveBeenCalledWith(latLng);
  });
});

describe('TSP reducers', () => {
  it('load demo', () => {
    expect(
      reducer(state.route, actions.loadDemo()),
    ).toEqual(state.route);
  });
  it('clear solution', () => {
    expect(
      reducer(state.route, actions.clearSolution()),
    ).toEqual(state.route);
  });
  it('add location', () => {
    expect(
      reducer(state.route, actions.addLocation(state.route[2])),
    ).toEqual(state.route);
  });
  it('delete location', () => {
    expect(
      reducer(state.route, actions.deleteLocation(1)),
    ).toEqual(state.route);
  });
  it('update solution', () => {
    expect(
      reducer(initialTspState, actions.updateTSPSolution(state.route)),
    ).toEqual(state.route);
  });
});

describe('TSP selectors', () => {
  it('domicile should be the first location ID', () => {
    expect(
      tspSelectors.getDomicileId(state.route),
    ).toEqual(1);
  });
  it('domicile should not be a positive number if route is empty', () => {
    expect(
      tspSelectors.getDomicileId(initialTspState),
    ).not.toBeGreaterThanOrEqual(0);
  });
});

const state: IAppState = {
  connectionStatus: WebSocketConnectionStatus.CLOSED,
  route: {
    distance: '10',
    route: [
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
