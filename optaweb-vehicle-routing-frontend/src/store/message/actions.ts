import { ActionFactory } from '../types';
import { ActionType, MessagePayload, ReadMessageAction, ReceiveMessageAction } from './types';

export const receiveMessage: ActionFactory<MessagePayload, ReceiveMessageAction> = (payload) => ({
  type: ActionType.RECEIVE_MESSAGE,
  payload,
});

export const readMessage: ActionFactory<string, ReadMessageAction> = (id) => ({
  type: ActionType.READ_MESSAGE,
  id,
});
