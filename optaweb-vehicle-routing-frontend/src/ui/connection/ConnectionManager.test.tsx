/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
