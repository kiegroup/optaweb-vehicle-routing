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

import { Button, TextInput } from '@patternfly/react-core';
import { shallow } from 'enzyme';
import toJson from 'enzyme-to-json';
import { OpenStreetMapProvider, SearchResult } from 'leaflet-geosearch';
import * as React from 'react';
import SearchBox, { Props, State } from './SearchBox';

jest.mock('leaflet-geosearch');
jest.useFakeTimers();

beforeEach(() => {
  // Clear all instances and calls to constructor and all methods:
  (OpenStreetMapProvider as unknown as jest.MockInstance<OpenStreetMapProvider, []>).mockClear();
});

const searchProviderMock = (
  () => (OpenStreetMapProvider as unknown as jest.MockInstance<OpenStreetMapProvider, []>).mock
);

describe('Search box', () => {
  it('should render text input initially', () => {
    const props: Props = {
      addHandler: jest.fn(),
      boundingBox: null,
      countryCodeSearchFilter: ['XY'],
      searchDelay: 1,
    };
    const searchBox = shallow(<SearchBox {...props} />);
    expect(toJson(searchBox)).toMatchSnapshot();
  });

  it('should show results when query is entered', async () => {
    const props: Props = {
      addHandler: jest.fn(),
      boundingBox: null,
      countryCodeSearchFilter: ['XY'],
      searchDelay: 1,
    };

    const searchBox = shallow(<SearchBox {...props} />);
    expect(searchProviderMock().instances).toHaveLength(1);
    // text input change triggers a component update
    searchBox.find(TextInput).simulate('change', 'London');
    // which in turn creates a new searchProvider instance
    expect(searchProviderMock().instances).toHaveLength(2);
    // so we can't provide the mock implementation earlier than here
    searchProviderMock().instances[1].search = jest.fn().mockImplementation(() => results);
    await jest.runAllTimers();
    expect(searchProviderMock().instances[1].search).toHaveBeenCalledTimes(1);
    expect((searchBox.state() as State).results).toHaveLength(results.length);
    expect((searchBox.state() as State).attributions).toEqual(licenses);
    expect(toJson(searchBox)).toMatchSnapshot();
  });

  it('should hide results when query is empty', () => {
    const props: Props = {
      addHandler: jest.fn(),
      boundingBox: null,
      countryCodeSearchFilter: ['XY'],
      searchDelay: 1,
    };

    const searchBox = shallow(<SearchBox {...props} />);
    expect(searchProviderMock().instances).toHaveLength(1);

    // when there are non-empty results
    searchBox.setState({ results, attributions: licenses });
    expect(searchProviderMock().instances).toHaveLength(2);
    expect((searchBox.state() as State).results).toEqual(results);
    expect((searchBox.state() as State).attributions).toEqual(licenses);

    // and an empty query is issued
    const emptyQuery = ' ';
    searchBox.find(TextInput).simulate('change', emptyQuery);
    expect(searchProviderMock().instances).toHaveLength(3);
    expect(toJson(searchBox)).toMatchSnapshot();

    // search is not invoked
    expect(searchProviderMock().instances[2].search).toHaveBeenCalledTimes(0);
    // and results are cleared
    expect(searchBox.state()).toEqual({ query: emptyQuery, results: [], attributions: [] });
  });

  it('should invoke add handler with the selected result and clear results', () => {
    const mockAddHandler = jest.fn();
    const props: Props = {
      addHandler: mockAddHandler,
      boundingBox: null,
      countryCodeSearchFilter: ['XY'],
      searchDelay: 1,
    };

    const searchBox = shallow(<SearchBox {...props} />);

    // when there are non-empty results
    searchBox.setState({ results, attributions: licenses });
    expect(toJson(searchBox)).toMatchSnapshot();

    const resultItems = searchBox.findWhere(
      node => node.key() !== null && node.key().startsWith('result'),
    );
    expect(resultItems).toHaveLength(results.length);

    const selection = results.length / 2;
    resultItems.at(selection).find(Button).simulate('click');
    expect(props.addHandler).toHaveBeenLastCalledWith(results[selection]);

    expect(searchBox.state()).toEqual({ query: '', results: [], attributions: [] });
  });
});

const licenses = ['License 1', 'License 2'];

const results: SearchResult[] = [{
  label: 'London, ON, Canada',
  x: 101,
  y: 102,
  bounds: [[1, 2], [3, 4]],
  raw: { licence: licenses[0] },
}, {
  label: 'London, OH, USA',
  x: 201,
  y: 202,
  bounds: [[1, 2], [3, 4]],
  raw: { licence: licenses[1] },
}, {
  label: 'London, KY, USA',
  x: 301,
  y: 302,
  bounds: [[1, 2], [3, 4]],
  raw: { licence: licenses[1] },
}, {
  label: 'London, UK',
  x: 401,
  y: 402,
  bounds: [[1, 2], [3, 4]],
  raw: { licence: licenses[0] },
}];
