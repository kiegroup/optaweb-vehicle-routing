import { shallow } from 'enzyme';
import toJson from 'enzyme-to-json';
import * as React from 'react';
import { WebSocketConnectionStatus } from 'store/websocket/types';
import { ConnectionManager, Props } from './ConnectionManager';

describe('Connection Manager', () => {
  it('should connect the WebSocket client when mounted', () => {
    const props: Props = {
      connectClient: jest.fn(),
      connectionStatus: WebSocketConnectionStatus.CLOSED,
    };

    const connectionManager = shallow(<ConnectionManager {...props} />);
    expect(toJson(connectionManager)).toMatchSnapshot();

    // Connect WebSocket client when the component is mounted
    expect(props.connectClient).toHaveBeenCalled();
  });

  it('should not display error when connection is open', () => {
    const props: Props = {
      connectClient: jest.fn(),
      connectionStatus: WebSocketConnectionStatus.OPEN,
    };

    const connectionManager = shallow(<ConnectionManager {...props} />);
    expect(toJson(connectionManager)).toMatchSnapshot();
  });

  it('should display error when connection fails', () => {
    const props: Props = {
      connectClient: jest.fn(),
      connectionStatus: WebSocketConnectionStatus.ERROR,
    };

    const connectionManager = shallow(<ConnectionManager {...props} />);
    expect(toJson(connectionManager)).toMatchSnapshot();
  });
});
