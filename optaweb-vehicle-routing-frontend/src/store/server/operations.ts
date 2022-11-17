import { resetViewport } from '../client/actions';
import { ResetViewportAction } from '../client/types';
import { Dispatch, ThunkCommandFactory } from '../types';
import * as actions from './actions';
import { ServerInfo, ServerInfoAction } from './types';

type ServerInfoThunkAction = ServerInfoAction | ResetViewportAction;

export const serverInfo: ThunkCommandFactory<ServerInfo, ServerInfoThunkAction> = (
  (info) => (dispatch: Dispatch<ServerInfoThunkAction>): void => {
    dispatch(resetViewport());
    dispatch(actions.serverInfo(info));
  });
