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

import { applyMiddleware, combineReducers, createStore, Store } from 'redux';
// it's possible to disable the extension in production
// by importing from redux-devtools-extension/developmentOnly
import { composeWithDevTools } from 'redux-devtools-extension';
import { createLogger } from 'redux-logger';
import thunk from 'redux-thunk';
import WebSocketClient from 'websocket/WebSocketClient';
import clientReducer from './client';
import demoReducer from './demo';
import routeReducer from './route';
import serverInfoReducer from './server';
import { AppState } from './types';
import connectionReducer from './websocket';

export interface StoreConfig {
  readonly socketUrl: string;
}

export function configureStore(
  { socketUrl }: StoreConfig,
  preloadedState?: AppState,
): Store<AppState> {
  const webSocketClient = new WebSocketClient(socketUrl);

  const middlewares = [thunk.withExtraArgument(webSocketClient), createLogger()];
  const middlewareEnhancer = applyMiddleware(...middlewares);

  const enhancers = [middlewareEnhancer];
  const composedEnhancers = composeWithDevTools(...enhancers);

  // map reducers to state slices
  const rootReducer = combineReducers<AppState>({
    connectionStatus: connectionReducer,
    serverInfo: serverInfoReducer,
    demo: demoReducer,
    plan: routeReducer,
    userViewport: clientReducer,
  });

  /* if (process.env.NODE_ENV !== 'production' && module.hot) {
    module.hot.accept('./reducers', () => store.replaceReducer(rootReducer));
  } */

  return createStore(
    rootReducer,
    preloadedState,
    composedEnhancers,
  );
}
