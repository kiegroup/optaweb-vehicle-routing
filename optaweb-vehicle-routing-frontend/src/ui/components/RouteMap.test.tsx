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
import RouteMap, { Props } from './RouteMap';

describe('Route Map', () => {
  it('should show the whole world when bounding box is null', () => {
    const props: Props = {
      updateViewport: jest.fn,
      clickHandler: jest.fn(),
      removeHandler: jest.fn(),
      selectedId: 1,
      depot: {
        id: 1,
        lat: 1.345678,
        lng: 1.345678,
      },
      visits: [],
      routes: [{
        visits: [],
        track: [],
      }],
      boundingBox: null,
      userViewport: {
        isDirty: false,
        zoom: 4,
        center: [1, 1],
      },
    };
    const routeMap = shallow(<RouteMap {...props} />);
    expect(toJson(routeMap)).toMatchSnapshot();
  });

  it('should pan and zoom to show bounding box if viewport is not dirty', () => {
    const depot = {
      id: 1,
      lat: 1.345678,
      lng: 1.345678,
    };
    const visit2 = {
      id: 2,
      lat: 2.345678,
      lng: 2.345678,
    };
    const visit3 = {
      id: 3,
      lat: 3.676111,
      lng: 3.568333,
    };
    const props: Props = {
      updateViewport: jest.fn(),
      clickHandler: jest.fn(),
      removeHandler: jest.fn(),
      selectedId: 1,
      boundingBox: [{ lat: -1, lng: -2 }, { lat: 10, lng: 20 }],
      userViewport: {
        isDirty: false,
        zoom: 4,
        center: [1, 1],
      },
      depot,
      visits: [visit2, visit3],
      routes: [{
        visits: [visit2, visit3],
        track: [[0.111222, 0.222333], [0.444555, 0.555666]],
      }],
    };
    const routeMap = shallow(<RouteMap {...props} />);
    expect(toJson(routeMap)).toMatchSnapshot();
  });

  it('should ignore bounds if viewport is dirty', () => {
    const depot = {
      id: 1,
      lat: 1.345678,
      lng: 1.345678,
    };
    const props: Props = {
      updateViewport: jest.fn(),
      clickHandler: jest.fn(),
      removeHandler: jest.fn(),
      selectedId: NaN,
      boundingBox: [{ lat: -1, lng: -2 }, { lat: 10, lng: 20 }],
      userViewport: {
        isDirty: true,
        zoom: 4,
        center: [1, 1],
      },
      depot,
      visits: [],
      routes: [],
    };
    const routeMap = shallow(<RouteMap {...props} />);
    // Map's bounds should be undefined
    expect(toJson(routeMap)).toMatchSnapshot();
  });
});
