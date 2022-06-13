import { Action } from 'redux';

export enum ActionType {
  REQUEST_DEMO = 'REQUEST_DEMO',
  FINISH_LOADING = 'FINISH_LOADING',
}

export interface RequestDemoAction extends Action<ActionType.REQUEST_DEMO> {
  readonly name: string;
}

export interface FinishLoadingAction extends Action<ActionType.FINISH_LOADING> {
}

export type DemoAction =
  | RequestDemoAction
  | FinishLoadingAction;

export interface Demo {
  readonly isLoading: boolean;
  readonly demoName: string | null;
}
