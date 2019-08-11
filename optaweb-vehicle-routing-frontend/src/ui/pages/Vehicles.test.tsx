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
import { Props, Vehicles } from './Vehicles';

describe('Vehicles page', () => {
  it('should render correctly', () => {
    const props: Props = {
      addVehicleHandler: jest.fn(),
      removeVehicleHandler: jest.fn(),
      changeVehicleCapacityHandler: jest.fn,
      vehicles: [
        { id: 1, name: 'Vehicle 1', capacity: 5 },
        { id: 2, name: 'Vehicle 2', capacity: 5 },
      ],
    };
    const vehicles = shallow(<Vehicles {...props} />);
    expect(toJson(vehicles)).toMatchSnapshot();
  });
});
