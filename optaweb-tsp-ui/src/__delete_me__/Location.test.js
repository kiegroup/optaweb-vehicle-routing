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
import React from 'react';
import Location from '../Location';

describe('Location Component', () => {
  it('should render correctly', () => {
    const props = {
      id: 10,
      removeDisabled: false,
      removeHandler: jest.fn(),
      selectHandler: jest.fn(),
    };
    expect.assertions(2);
    const location = shallow(<Location {...props} />);
    expect(location).toMatchSnapshot();

    location.find('button').simulate('click');
    location.find('div').simulate('mouseEnter');

    expect(props.removeHandler).toHaveBeenCalledTimes(1);
  });
});
