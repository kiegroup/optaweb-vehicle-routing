import { ActionType, ServerInfo, ServerInfoAction } from './types';

export const initialServerState: ServerInfo = {
  boundingBox: null,
  countryCodes: [],
  demos: [],
};

export const routeReducer = (state = initialServerState, action: ServerInfoAction): ServerInfo => {
  switch (action.type) {
    case ActionType.SERVER_INFO: {
      return action.value;
    }
    default:
      return state;
  }
};
