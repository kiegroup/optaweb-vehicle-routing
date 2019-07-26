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
import { Route, RouteProps } from './Route';

describe('Route page', () => {
  it('should render correctly with no routes', () => {
    const routes = shallow(<Route {...noRoutes} />);
    expect(toJson(routes)).toMatchSnapshot();
  });

  it('should render correctly with a few routes', () => {
    const routes = shallow(<Route {...twoRoutes} />);
    expect(toJson(routes)).toMatchSnapshot();
  });
});

const noRoutes: RouteProps = {
  addHandler: jest.fn(),
  removeHandler: jest.fn(),

  boundingBox: null,

  depot: null,
  routes: [],
};

const twoRoutes: RouteProps = {
  addHandler: jest.fn(),
  removeHandler: jest.fn(),

  boundingBox: null,

  depot: {
    id: 1,
    lat: 1.345678,
    lng: 1.345678,
  },

  routes: [{
    vehicle: { id: 1, name: 'v1' },
    visits: [{
      id: 1,
      lat: 1.345678,
      lng: 1.345678,
    }, {
      id: 2,
      lat: 2.345678,
      lng: 2.345678,
    }, {
      id: 3,
      lat: 3.676111,
      lng: 3.568333,
    }],

    track: [],

  }, {
    vehicle: { id: 2, name: 'v2' },
    visits: [{
      id: 1,
      lat: 1.345678,
      lng: 1.345678,
    }, {
      id: 4,
      lat: 4.345678,
      lng: 4.345678,
    }, {
      id: 5,
      lat: 5.676111,
      lng: 5.568333,
    }],

    track: [],

  }],
};
