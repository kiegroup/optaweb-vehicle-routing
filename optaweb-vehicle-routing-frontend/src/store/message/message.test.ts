import { Message, MessagePayload } from 'store/message/types';
import * as actions from './actions';
import reducer, { messageSelectors } from './index';

const messages: Message[] = [
  { id: '1', text: '', status: 'read' },
  { id: '2', text: '', status: 'new' },
  { id: '3', text: '', status: 'read' },
  { id: '4', text: '', status: 'new' },
  { id: '5', text: '', status: 'new' },
];

describe('Message reducer', () => {
  it(
    'should return initial state when previous state is undefined',
    () => expect(reducer(undefined, actions.readMessage(''))).toEqual([]),
  );

  it('receiving a message should add it with the status equal to NEW', () => {
    const message: MessagePayload = { id: 'xy', text: 'message text' };
    expect(
      reducer([], actions.receiveMessage(message)),
    ).toEqual([{ ...message, status: 'new' }]);
  });

  it('reading a message should update its status to READ', () => {
    const updatedMessages = [...messages];
    updatedMessages[1] = { ...messages[1], status: 'read' };
    expect(
      reducer(messages, actions.readMessage('2')),
    ).toEqual(updatedMessages);
  });
});

describe('Message selectors', () => {
  it('select new messages', () => {
    expect(
      messageSelectors.getNewMessages(messages),
    ).toEqual([
      { id: '2', text: '', status: 'new' },
      { id: '4', text: '', status: 'new' },
      { id: '5', text: '', status: 'new' },
    ]);
  });
});
