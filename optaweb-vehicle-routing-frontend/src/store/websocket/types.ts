import { Action } from 'redux';

export enum WebSocketConnectionStatus {
  OPEN = 'OPEN',
  CLOSED = 'CLOSED',
  ERROR = 'ERROR',
}

export enum ActionType {
  WS_CONNECT = 'WS_CONNECT',
  WS_CONNECT_SUCCESS = 'WS_CONNECT_SUCCESS',
  WS_CONNECT_FAILURE = 'WS_CONNECT_FAILURE',
}

export interface InitWsConnectionAction extends Action<ActionType.WS_CONNECT> {
}

export interface WsConnectionSuccessAction extends Action<ActionType.WS_CONNECT_SUCCESS> {
}

export interface WsConnectionFailureAction extends Action<ActionType.WS_CONNECT_FAILURE> {
  readonly error: string;
}

export type WebSocketAction =
  | InitWsConnectionAction
  | WsConnectionFailureAction
  | WsConnectionSuccessAction;
