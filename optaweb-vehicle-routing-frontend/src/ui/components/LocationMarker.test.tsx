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
import { Marker } from 'react-leaflet';
import { Location } from 'store/route/types';
import LocationMarker, { Props } from './LocationMarker';

const location: Location = {
  id: 1,
  lat: 1.345678,
  lng: 1.345678,
};

describe('Location Marker', () => {
  it('render depot', () => {
    const props: Props = {
      removeHandler: jest.fn(),
      isDepot: true,
      isSelected: false,
      location,
    };
    const locationMarker = shallow(<LocationMarker {...props} />);
    expect(toJson(locationMarker)).toMatchSnapshot();
  });

  it('render visit', () => {
    const props: Props = {
      removeHandler: jest.fn(),
      isDepot: false,
      isSelected: false,
      location,
    };
    const locationMarker = shallow(<LocationMarker {...props} />);
    expect(toJson(locationMarker)).toMatchSnapshot();
  });

  it('selected visit should show a tooltip', () => {
    const props: Props = {
      removeHandler: jest.fn(),
      isDepot: false,
      isSelected: true,
      location,
    };
    const locationMarker = shallow(<LocationMarker {...props} />);
    expect(toJson(locationMarker)).toMatchSnapshot();
  });

  it('should call remove handler when clicked', () => {
    const props: Props = {
      removeHandler: jest.fn(),
      isDepot: false,
      isSelected: true,
      location,
    };
    const locationMarker = shallow(<LocationMarker {...props} />);
    locationMarker.find(Marker).simulate('click');
    expect(props.removeHandler).toBeCalled();
  });
});
