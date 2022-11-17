import { Message } from 'store/message/types';

export const getNewMessages = (messages: Message[]): Message[] => (
  messages.filter((message) => message.status === 'new')
);
