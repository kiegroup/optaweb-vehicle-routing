import { ActionFactory } from '../types';
import { ActionType, FinishLoadingAction, RequestDemoAction } from './types';

export const requestDemo: ActionFactory<string, RequestDemoAction> = (name) => ({
  type: ActionType.REQUEST_DEMO,
  name,
});

export const finishLoading: ActionFactory<void, FinishLoadingAction> = () => ({
  type: ActionType.FINISH_LOADING,
});
