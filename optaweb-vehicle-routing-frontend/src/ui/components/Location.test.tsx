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

import { Button, DataListItem } from '@patternfly/react-core';
import { shallow } from 'enzyme';
import toJson from 'enzyme-to-json';
import * as React from 'react';
import Location, { LocationProps } from './Location';

describe('Location Component', () => {
  it('should render correctly', () => {
    const props: LocationProps = {
      id: 10,
      description: 'x',
      removeDisabled: false,
      removeHandler: jest.fn(),
      selectHandler: jest.fn(),
    };
    const location = shallow(<Location {...props} />);
    expect(toJson(location)).toMatchSnapshot();
    location.find(DataListItem).simulate('mouseEnter');
    location.find(Button).simulate('click');

    expect(props.removeHandler).toHaveBeenCalledTimes(1);
    expect(props.selectHandler).toHaveBeenCalledTimes(1);
  });

  it('should render correctly when description is missing', () => {
    const props: LocationProps = {
      id: 11,
      description: null,
      removeDisabled: false,
      removeHandler: jest.fn(),
      selectHandler: jest.fn(),
    };
    const location = shallow(<Location {...props} />);
    expect(toJson(location)).toMatchSnapshot();
  });
});
