import { ActionType, WebSocketAction, WebSocketConnectionStatus } from './types';

export const wsReducer = (
// eslint-disable-next-line @typescript-eslint/default-param-last
  (state = WebSocketConnectionStatus.CLOSED, action: WebSocketAction): WebSocketConnectionStatus => {
    switch (action.type) {
      case ActionType.WS_CONNECT_SUCCESS: {
        return WebSocketConnectionStatus.OPEN;
      }
      case ActionType.WS_CONNECT_FAILURE: {
        return WebSocketConnectionStatus.ERROR;
      }
      default:
        return state;
    }
  });
