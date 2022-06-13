import { resetViewport } from '../client/actions';
import { ResetViewportAction } from '../client/types';
import { ThunkCommandFactory } from '../types';
import * as actions from './actions';
import { ServerInfo, ServerInfoAction } from './types';

export const serverInfo: ThunkCommandFactory<ServerInfo, ServerInfoAction | ResetViewportAction> = (
  (info) => (dispatch) => {
    dispatch(resetViewport());
    dispatch(actions.serverInfo(info));
  });
