import { ActionType, Message, MessageAction } from './types';

// eslint-disable-next-line @typescript-eslint/default-param-last
export const messageReducer = (state: Message[] = [], action: MessageAction): Message[] => {
  switch (action.type) {
    case ActionType.RECEIVE_MESSAGE: {
      return [...state, { ...action.payload, status: 'new' }];
    }
    case ActionType.READ_MESSAGE: {
      return state.map((message) => (
        message.id === action.id ? { ...message, status: 'read' } : message
      ));
    }
    default:
      return state;
  }
};
