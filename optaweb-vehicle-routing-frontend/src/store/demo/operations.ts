import { AppState, Dispatch, ThunkCommandFactory } from '../types';
import * as actions from './actions';
import { RequestDemoAction } from './types';
import WebSocketClient from '../../websocket/WebSocketClient';

export const { finishLoading } = actions;

export const requestDemo: ThunkCommandFactory<string, RequestDemoAction> = (
  (name) => (dispatch: Dispatch<RequestDemoAction>, _getState: () => AppState, client: WebSocketClient): void => {
    dispatch(actions.requestDemo(name));
    client.loadDemo(name);
  });
