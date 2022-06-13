import { ActionFactory } from '../types';
import { ActionType, ServerInfo, ServerInfoAction } from './types';

export const serverInfo: ActionFactory<ServerInfo, ServerInfoAction> = (info) => ({
  type: ActionType.SERVER_INFO,
  value: info,
});
