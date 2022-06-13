import { applyMiddleware, combineReducers, createStore, Store } from 'redux';
// it's possible to disable the extension in production
// by importing from redux-devtools-extension/developmentOnly
import { composeWithDevTools } from 'redux-devtools-extension';
import { createLogger } from 'redux-logger';
import thunk from 'redux-thunk';
import WebSocketClient from 'websocket/WebSocketClient';
import clientReducer from './client';
import demoReducer from './demo';
import messageReducer from './message';
import routeReducer from './route';
import serverInfoReducer from './server';
import { AppState } from './types';
import connectionReducer from './websocket';

export interface StoreConfig {
  readonly backendUrl: string;
}

export function configureStore(
  { backendUrl }: StoreConfig,
  preloadedState?: AppState,
): Store<AppState> {
  const webSocketClient = new WebSocketClient(backendUrl);

  const middlewares = [thunk.withExtraArgument(webSocketClient), createLogger()];
  const middlewareEnhancer = applyMiddleware(...middlewares);

  const enhancers = [middlewareEnhancer];
  const composedEnhancers = composeWithDevTools(...enhancers);

  // map reducers to state slices
  const rootReducer = combineReducers<AppState>({
    connectionStatus: connectionReducer,
    messages: messageReducer,
    serverInfo: serverInfoReducer,
    demo: demoReducer,
    plan: routeReducer,
    userViewport: clientReducer,
  });

  return createStore(
    rootReducer,
    preloadedState,
    composedEnhancers,
  );
}
