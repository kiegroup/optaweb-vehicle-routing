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
import { OpenStreetMapProvider, SearchResult } from 'leaflet-geosearch';
import * as React from 'react';
import SearchBox, { IProps, IState } from './SearchBox';

jest.mock('leaflet-geosearch');
jest.useFakeTimers();

beforeEach(() => {
  (OpenStreetMapProvider as unknown as jest.MockInstance<OpenStreetMapProvider>).mockReset();
});

describe('Search box', () => {
  it('should render text input initially', () => {
    const props: IProps = {
      addHandler: jest.fn(),
      searchDelay: 1,
    };
    const searchBox = shallow(<SearchBox {...props} />);
    expect(toJson(searchBox)).toMatchSnapshot();
  });

  it('should show results when query is entered', async () => {
    const props: IProps = {
      addHandler: jest.fn(),
      searchDelay: 1,
    };

    const results: SearchResult[] = [
      {
        bounds: [[1, 2], [3, 4]],
        label: 'London, ON, Canada',
        raw: { licence: 'License 1' },
        x: 101,
        y: 102,
      },
      {
        bounds: [[1, 2], [3, 4]],
        label: 'London, OH, USA',
        raw: { licence: 'License 2' },
        x: 201,
        y: 202,
      },
      {
        bounds: [[1, 2], [3, 4]],
        label: 'London, KY, USA',
        raw: { licence: 'License 2' },
        x: 301,
        y: 302,
      },
      {
        bounds: [[1, 2], [3, 4]],
        label: 'London, UK',
        raw: { licence: 'License 1' },
        x: 401,
        y: 402,
      },
    ];

    const searchBox = shallow(<SearchBox {...props} />);
    // @ts-ignore
    OpenStreetMapProvider.mock.instances[0].search.mockImplementation(() => results);
    searchBox.find('TextInput').simulate('change', 'London');
    await jest.runAllTimers();
    expect(toJson(searchBox)).toMatchSnapshot();
    // @ts-ignore
    expect(OpenStreetMapProvider.mock.instances[0].search).toHaveBeenCalledTimes(1);
    expect((searchBox.state() as IState).results).toHaveLength(results.length);
    expect((searchBox.state() as IState).attributions).toEqual(['License 1', 'License 2']);

    // when query is empty
    searchBox.find('TextInput').simulate('change', ' ');
    await jest.runAllTimers();
    expect(toJson(searchBox)).toMatchSnapshot();
    // search is not invoked
    // @ts-ignore
    expect(OpenStreetMapProvider.mock.instances[0].search).toHaveBeenCalledTimes(1);
    // results are empty
    expect((searchBox.state() as IState).results).toEqual([]);
    expect((searchBox.state() as IState).attributions).toEqual([]);
  });
});
