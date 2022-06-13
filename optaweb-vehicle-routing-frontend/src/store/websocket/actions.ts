import { ActionFactory } from '../types';
import { ActionType, InitWsConnectionAction, WsConnectionFailureAction, WsConnectionSuccessAction } from './types';

export const initWsConnection: ActionFactory<void, InitWsConnectionAction> = () => ({
  type: ActionType.WS_CONNECT,
});

export const wsConnectionSuccess: ActionFactory<void, WsConnectionSuccessAction> = () => ({
  type: ActionType.WS_CONNECT_SUCCESS,
});

export const wsConnectionFailure: ActionFactory<string, WsConnectionFailureAction> = (error) => ({
  type: ActionType.WS_CONNECT_FAILURE,
  error,
});
