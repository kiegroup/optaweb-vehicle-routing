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

import {
  applyMiddleware, combineReducers, compose, createStore,
} from 'redux';
import { createLogger } from 'redux-logger';
import tspReducer, { ITSPRoute, tspOperations } from './tsp/index';

export interface IAppState {
  tsp: ITSPRoute;
}

export interface IAppStoreConfig {
  socketUrl: string;
}

export default function configureStore(
  { socketUrl }: IAppStoreConfig,
  preloadedState?: IAppState,
) {
  // create logger middleware
  const logger = createLogger();

  /* eslint-disable no-underscore-dangle */
  const composeEnhancers = (window as any).__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ || compose;
  /* eslint-enable */

  // combining reducers
  const rootReducer = combineReducers({ tsp: tspReducer });

  const store = createStore(
    rootReducer,
    preloadedState,
    composeEnhancers(applyMiddleware(logger)),
  );

  tspOperations.connect({
    dispatch: store.dispatch,
    socketUrl,
  });

  /* if (process.env.NODE_ENV !== 'production' && module.hot) {
    module.hot.accept('./reducers', () => store.replaceReducer(rootReducer));
  } */

  return store;
}
