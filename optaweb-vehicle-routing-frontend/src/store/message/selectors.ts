import { Message } from 'store/message/types';

export const getNewMessages = (messages: Message[]) => messages.filter((message) => message.status === 'new');
