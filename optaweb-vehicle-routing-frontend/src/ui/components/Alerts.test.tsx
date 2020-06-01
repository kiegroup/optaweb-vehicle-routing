/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import { Alert } from '@patternfly/react-core';
import { shallow } from 'enzyme';
import toJson from 'enzyme-to-json';
import * as React from 'react';
import { ReactElement } from 'react';
import { Alerts, Props } from 'ui/components/Alerts';

describe('Alerts', () => {
  it('should call readMessage() when alert is closed', () => {
    const props: Props = {
      messages: [
        { id: '1', text: 'msg 1', status: 'new' },
        { id: '2', text: 'msg 2', status: 'new' },
      ],
      readMessage: jest.fn(),
    };
    const alerts = shallow(<Alerts {...props} />);
    expect(toJson(alerts)).toMatchSnapshot();

    (alerts.find(Alert).at(1).prop('action') as ReactElement).props.onClose();

    expect(props.readMessage).toHaveBeenCalledWith('2');
  });

  it('should not render if there are no messages', () => {
    const props: Props = {
      messages: [],
      readMessage: jest.fn(),
    };
    const alerts = shallow(<Alerts {...props} />);
    expect(toJson(alerts)).toMatchSnapshot();
  });
});
