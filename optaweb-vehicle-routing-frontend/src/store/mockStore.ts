import { Middleware } from 'redux';
import createMockStore, { MockStoreCreator } from 'redux-mock-store';
import thunk, { ThunkDispatch } from 'redux-thunk';
import WebSocketClient from '../websocket/WebSocketClient';
import { UpdateRouteAction } from './route/types';
import { AppState } from './types';
import { WebSocketAction } from './websocket/types';

jest.mock('../websocket/WebSocketClient');

export const mockStore = (state: AppState) => {
  const client = new WebSocketClient('');
  const middlewares: Middleware[] = [thunk.withExtraArgument(client)];
  type DispatchExts = ThunkDispatch<AppState, WebSocketClient, WebSocketAction | UpdateRouteAction>;
  const mockStoreCreator: MockStoreCreator<AppState, DispatchExts> = (
    createMockStore<AppState, DispatchExts>(middlewares)
  );
  return { store: mockStoreCreator(state), client };
};
