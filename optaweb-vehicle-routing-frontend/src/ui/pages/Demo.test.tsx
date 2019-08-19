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

import { Button, Dropdown } from '@patternfly/react-core';
import { shallow } from 'enzyme';
import toJson from 'enzyme-to-json';
import * as React from 'react';
import { UserViewport } from 'store/client/types';
import { Demo, DemoProps, ID_CLEAR_BUTTON, ID_EXPORT_BUTTON } from './Demo';

describe('Demo page', () => {
  it('should render correctly with no routes', () => {
    const demo = shallow(<Demo {...emptyRouteProps} />);
    expect(toJson(demo)).toMatchSnapshot();
  });

  it('should render correctly with a few routes', () => {
    const demo = shallow(<Demo {...threeLocationsProps} />);
    expect(toJson(demo)).toMatchSnapshot();
  });

  it('clear and export buttons should be disabled when demo is loading', () => {
    const props: DemoProps = {
      ...threeLocationsProps,
      isDemoLoading: true,
    };
    const demo = shallow(<Demo {...props} />);
    expect(toJson(demo)).toMatchSnapshot();

    const clearButton = demo.find(Button).filter(`#${ID_CLEAR_BUTTON}`);
    expect(clearButton.props().isDisabled).toEqual(true);

    clearButton.simulate('click');
    // Doesn't work, probably due to https://github.com/airbnb/enzyme/issues/386
    // expect(props.clearHandler).not.toHaveBeenCalled();

    const exportButton = demo.find(Button).filter(`#${ID_EXPORT_BUTTON}`);
    expect(exportButton.props().isDisabled).toEqual(true);
  });

  it('clear button should replace demo dropdown as soon as there is a depot', () => {
    const props: DemoProps = {
      ...emptyRouteProps,
      depot: {
        id: 1,
        lat: 1,
        lng: 1,
        description: '',
      },
    };
    const demo = shallow(<Demo {...props} />);
    expect(toJson(demo)).toMatchSnapshot();

    const clearButton = demo.find(Button).filter(`#${ID_CLEAR_BUTTON}`);
    expect(clearButton).toHaveLength(1);
    expect(clearButton.props().isDisabled).toEqual(false);

    const exportButton = demo.find(Button).filter(`#${ID_EXPORT_BUTTON}`);
    expect(exportButton).toHaveLength(1);
    expect(exportButton.props().isDisabled).toEqual(false);

    expect(demo.find(Dropdown)).toHaveLength(0);
  });
});

const userViewport: UserViewport = {
  isDirty: false,
  zoom: 1,
  center: [0, 0],
};

const emptyRouteProps: DemoProps = {
  loadHandler: jest.fn(),
  clearHandler: jest.fn(),
  addVehicleHandler: jest.fn,
  removeVehicleHandler: jest.fn,
  addLocationHandler: jest.fn(),
  removeLocationHandler: jest.fn(),
  updateViewport: jest.fn(),

  distance: '0',
  vehicleCount: 0,
  demoNames: ['demo'],
  isDemoLoading: false,
  boundingBox: null,
  userViewport,
  countryCodeSearchFilter: [],

  depot: null,
  routes: [],
  visits: [],
};

const threeLocationsProps: DemoProps = {
  loadHandler: jest.fn(),
  clearHandler: jest.fn(),
  addVehicleHandler: jest.fn,
  removeVehicleHandler: jest.fn,
  addLocationHandler: jest.fn(),
  removeLocationHandler: jest.fn(),
  updateViewport: jest.fn(),

  distance: '10',
  vehicleCount: 8,
  demoNames: ['demo'],
  isDemoLoading: false,
  boundingBox: null,
  userViewport,
  countryCodeSearchFilter: ['XY'],

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

  routes: [{
    vehicle: { id: 1, name: 'v1', capacity: 5 },
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

  }],
};
