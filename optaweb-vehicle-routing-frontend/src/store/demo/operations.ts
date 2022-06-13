import { ThunkCommandFactory } from '../types';
import * as actions from './actions';
import { RequestDemoAction } from './types';

export const { finishLoading } = actions;

export const requestDemo: ThunkCommandFactory<string, RequestDemoAction> = (
  (name) => (dispatch, getState, client) => {
    dispatch(actions.requestDemo(name));
    client.loadDemo(name);
  });
