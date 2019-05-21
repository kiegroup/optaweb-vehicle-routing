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
import { Props, Visits } from './Visits';

describe('Visits page', () => {
  it('should render correctly with no visits', () => {
    const visits = shallow(<Visits {...noVisits} />);
    expect(toJson(visits)).toMatchSnapshot();
  });

  it('should render correctly with a few visits', () => {
    const visits = shallow(<Visits {...twoVisits} />);
    expect(toJson(visits)).toMatchSnapshot();
  });
});

const noVisits: Props = {
  addHandler: jest.fn(),
  removeHandler: jest.fn(),

  depot: null,
  visits: [],
};

const twoVisits: Props = {
  addHandler: jest.fn(),
  removeHandler: jest.fn(),

  depot: {
    id: 1,
    lat: 1.345678,
    lng: 1.345678,
  },

  visits: [{
    id: 2,
    lat: 2.345678,
    lng: 2.345678,
  }, {
    id: 3,
    lat: 3.676111,
    lng: 3.568333,
  }],
};
