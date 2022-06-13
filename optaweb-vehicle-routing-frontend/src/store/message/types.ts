import { Action } from 'redux';

export enum ActionType {
  RECEIVE_MESSAGE = 'RECEIVE_MESSAGE',
  READ_MESSAGE = 'READ_MESSAGE',
}

export interface ReceiveMessageAction extends Action<ActionType.RECEIVE_MESSAGE> {
  readonly payload: MessagePayload;
}

export interface ReadMessageAction extends Action<ActionType.READ_MESSAGE> {
  readonly id: string;
}

export type MessageAction = ReceiveMessageAction | ReadMessageAction;

export interface MessagePayload {
  readonly id: string;
  readonly text: string;
}

export interface Message extends MessagePayload {
  readonly status: 'new' | 'read';
}
