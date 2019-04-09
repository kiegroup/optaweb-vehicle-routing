/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
import ConnectionError, { IConnectionErrorProps } from './ConnectionError';

describe('Connection Error Component', () => {
  it('should render correctly', () => {
    const props: IConnectionErrorProps = {
      title: 'title',
      message: 'message',
      icon: <i>Icon</i>,
      help: 'help',
      isOpen: true,
    };

    expect.assertions(1);
    const connectionError = shallow(<ConnectionError {...props} />);
    expect(toJson(connectionError)).toMatchSnapshot();
  });
});
