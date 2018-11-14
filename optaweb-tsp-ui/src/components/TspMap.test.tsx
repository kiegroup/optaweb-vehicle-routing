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
import TspMap from './TspMap';

describe('TSP Map View', () => {
  it('should render correctly', () => {
    const props = {
      center: {
        lat: 1.345678,
        lng: 1.345678,
      },
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
      clickHandler: jest.fn(),
      removeHandler: jest.fn(),
      zoom: 5,
      selectedId: 1,
    };
    expect.assertions(1);
    const tspMap = shallow(<TspMap {...props} />);
    expect(tspMap).toMatchSnapshot();
  });
});
