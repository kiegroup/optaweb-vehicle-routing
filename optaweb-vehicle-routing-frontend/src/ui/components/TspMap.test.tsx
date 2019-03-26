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
import TspMap, { ITspMapProps } from './TspMap';

describe('TSP Map View', () => {
  it('should render correctly', () => {
    const props: ITspMapProps = {
      center: {
        lat: 1.345678,
        lng: 1.345678,
      },
      clickHandler: jest.fn(),
      removeHandler: jest.fn(),

      depot: {
        id: 1,
        lat: 1.345678,
        lng: 1.345678,
      },
      routes: [{
        visits: [
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

        track: [{ lat: 0.111222, lng: 0.222333 }, { lat: 0.444555, lng: 0.555666 }],
      }],
      selectedId: 1,
      zoom: 5,
    };
    expect.assertions(1);
    const tspMap = shallow(<TspMap {...props} />);
    expect(toJson(tspMap)).toMatchSnapshot();
  });
});
