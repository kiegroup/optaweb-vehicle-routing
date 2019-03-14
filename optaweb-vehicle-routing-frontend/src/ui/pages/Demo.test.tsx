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

import { Button } from '@patternfly/react-core';
import { shallow } from 'enzyme';
import toJson from 'enzyme-to-json';
import * as React from 'react';
import { Demo, IDemoProps } from './Demo';

describe('Demo page', () => {
  it('should render correctly with no routes', () => {
    const demo = shallow(<Demo {...emptyRouteProps} />);
    expect(toJson(demo)).toMatchSnapshot();

    demo.find(Button).simulate('click');

    expect(emptyRouteProps.loadHandler).toHaveBeenCalledTimes(1);
  });

  it('should render correctly with a few routes', () => {
    const demo = shallow(<Demo {...threeLocationsProps} />);
    expect(toJson(demo)).toMatchSnapshot();
  });

  it('clear button should be disabled when demo is loading', () => {
    const props: IDemoProps = {
      ...threeLocationsProps,
      isDemoLoading: true,
    };
    const demo = shallow(<Demo {...props} />);
    expect(toJson(demo)).toMatchSnapshot();

    const clearButton = demo.find(Button);
    expect(clearButton.props().isDisabled).toEqual(true);

    clearButton.simulate('click');
    // Doesn't work, probably due to https://github.com/airbnb/enzyme/issues/386
    // expect(props.clearHandler).not.toHaveBeenCalled();
  });
});

const emptyRouteProps: IDemoProps = {
  addHandler: jest.fn(),
  clearHandler: jest.fn(),
  isDemoLoading: false,
  loadHandler: jest.fn(),
  removeHandler: jest.fn(),

  distance: '0',
  locations: [],
  routes: [],
};

const threeLocationsProps: IDemoProps = {
  addHandler: jest.fn(),
  clearHandler: jest.fn(),
  isDemoLoading: false,
  loadHandler: jest.fn(),
  removeHandler: jest.fn(),

  depot: {
    id: 1,
    lat: 1.345678,
    lng: 1.345678,
  },
  distance: '10',
  locations: [{
    id: 2,
    lat: 2.345678,
    lng: 2.345678,
  },
    {
      id: 3,
      lat: 3.676111,
      lng: 3.568333,
    }],
  routes: [{
    visits: [
      {
        id: 1,
        lat: 1.345678,
        lng: 1.345678,
      },
      {
        id: 2,
        lat: 2.345678,
        lng: 2.345678,
      },
      {
        id: 3,
        lat: 3.676111,
        lng: 3.568333,
      },
    ],

    track: [],
  }],
};
