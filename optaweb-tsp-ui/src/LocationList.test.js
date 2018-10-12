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

import React from 'react';
import { shallow } from 'enzyme';
import LocationList from './LocationList';

describe('Location List Component Test', () => {
  it('should render correctly with no routes', () => {
    const mockLocationList = {
      route: [],
      domicileId: 1,
      distance: '10',
      removeHandler: jest.fn(() => {}),
      selectHandler: jest.fn(select => select),
      loadHandler: jest.fn(() => {}),
    };
    expect.assertions(2);
    const LocationListMock = shallow(<LocationList {...mockLocationList} />);
    expect(LocationListMock).toMatchSnapshot();

    LocationListMock.find('button').simulate('click');

    expect(mockLocationList.loadHandler.mock.calls.length).toBe(1);
  });

  it('should render correctly with few routes', () => {
    const mockLocationList = {
      route: [
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
      domicileId: 1,
      distance: '10',
      removeHandler: jest.fn(() => {}),
      selectHandler: jest.fn(select => select),
      loadHandler: jest.fn(() => {}),
    };
    expect.assertions(2);
    const LocationMock = shallow(<LocationList {...mockLocationList} />);
    expect(LocationMock).toMatchSnapshot();

    expect(LocationMock.find('Location').length).toBe(mockLocationList.route.length);

    
  });
  
});
