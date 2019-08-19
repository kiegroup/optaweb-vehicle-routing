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
import { VehicleCapacity } from 'store/route/types';
import Vehicle, { VehicleProps } from './Vehicle';

describe('Vehicle Component', () => {
  it('should render correctly', () => {
    const props: VehicleProps = {
      id: 10,
      description: 'x',
      capacity: 7,
      removeHandler: jest.fn(),
      capacityChangeHandler: jest.fn(),
    };
    const vehicle = shallow(<Vehicle {...props} />);
    expect(toJson(vehicle)).toMatchSnapshot();

    vehicle.find(Button).filter(`[data-test-key="remove-${props.id}"]`).simulate('click');
    expect(props.removeHandler).toHaveBeenCalledTimes(1);

    vehicle.find(Button).filter(`[data-test-key="capacity-increase-${props.id}"]`).simulate('click');
    const increasedCapacity: VehicleCapacity = { vehicleId: props.id, capacity: props.capacity + 1 };
    expect(props.capacityChangeHandler).toHaveBeenCalledWith(increasedCapacity);

    vehicle.find(Button).filter(`[data-test-key="capacity-decrease-${props.id}"]`).simulate('click');
    const decreasedCapacity: VehicleCapacity = { vehicleId: props.id, capacity: props.capacity - 1 };
    expect(props.capacityChangeHandler).toHaveBeenCalledWith(decreasedCapacity);
  });
});
