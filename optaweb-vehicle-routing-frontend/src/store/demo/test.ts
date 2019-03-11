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
import { RouteAction } from '../route/types';
import { WebSocketAction, WebSocketConnectionStatus } from '../websocket/types';
import * as actions from './actions';
import reducer, { demoOperations } from './index';
import { IDemo, ILoadDemoAction } from './types';

jest.mock('websocket/WebSocketClient');

describe('Demo operations', () => {
  it('should dispatch actions and call client', () => {
    let demoCallbackCapture: (demoSize: number) => void = uninitializedCallbackCapture;

    const client = new WebSocketClient('');
    // @ts-ignore
    client.loadDemo.mockImplementation((demoCallback) => {
      demoCallbackCapture = demoCallback;
    });

    // mock store
    const middlewares: Middleware[] = [thunk.withExtraArgument(client)];
    type DispatchExts = ThunkDispatch<IAppState, WebSocketClient,
      WebSocketAction | RouteAction | ILoadDemoAction>;
    const mockStoreCreator: MockStoreCreator<IAppState, DispatchExts> =
      createMockStore<IAppState, DispatchExts>(middlewares);
    const store: MockStoreEnhanced<IAppState, DispatchExts> = mockStoreCreator(state);

    // verify loadDemo operation calls the client
    store.dispatch(demoOperations.loadDemo());
    expect(client.loadDemo).toHaveBeenCalledTimes(1);

    // simulate client receives demo size and passes it to demo callback
    const demoSize = 314354;
    demoCallbackCapture(demoSize);
    expect(store.getActions()).toEqual([actions.loadDemo(demoSize)]);
  });
});

describe('Demo reducers', () => {
  it('load demo', () => {
    const expectedState: IDemo = { isLoading: true, demoSize: 5 };
    expect(
      reducer(state.demo, actions.loadDemo(expectedState.demoSize)),
    ).toEqual(expectedState);
  });
  it('loading flag should be cleared when demo is loaded', () => {
    const demoSize: number = 5;
    const expectedState: IDemo = { isLoading: false, demoSize };
    expect(
      reducer({ isLoading: true, demoSize }, actions.demoLoaded()),
    ).toEqual(expectedState);
  });
});

const state: IAppState = {
  connectionStatus: WebSocketConnectionStatus.CLOSED,
  demo: {
    demoSize: 0,
    isLoading: false,
  },
  plan: {
    distance: '10',
    routes: [
      {
        visits: [
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

        track: [{ lat: 0.111222, lng: 0.222333 }, { lat: 0.444555, lng: 0.555666 }],
      },
    ],
  },
};

const uninitializedCallbackCapture = () => {
  throw new Error('Error callback is uninitialized');
};
