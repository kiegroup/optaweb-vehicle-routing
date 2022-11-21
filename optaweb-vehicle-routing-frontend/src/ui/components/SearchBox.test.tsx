import { render } from '@testing-library/react';
import { OpenStreetMapProvider } from 'leaflet-geosearch';
import { RawResult } from 'leaflet-geosearch/lib/providers/openStreetMapProvider';
import { SearchResult } from 'leaflet-geosearch/lib/providers/provider';
import { shallow, toJson } from 'ui/shallow-test-util';
import SearchBox, { Props, Result } from './SearchBox';

jest.mock('leaflet-geosearch');

beforeEach(() => {
  // Clear all instances and calls to constructor and all methods:
  (OpenStreetMapProvider as unknown as jest.MockInstance<OpenStreetMapProvider, []>).mockClear();
  jest.useFakeTimers();
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
    // const user = userEvent.setup();

    render(<SearchBox {...props} />);
    expect(searchProviderMock().instances).toHaveLength(1);
    // FIXME user.type() times out
    /*
    // text input change triggers a component update
    await user.type(screen.getByLabelText('geosearch text input'), 'London');
    // which in turn creates a new searchProvider instance
    expect(searchProviderMock().instances).toHaveLength(2);
    // so we can't provide the mock implementation earlier than here
    searchProviderMock().instances[1].search = jest.fn().mockImplementation(() => searchResults);
    await jest.runAllTimers();
    expect(searchProviderMock().instances[1].search).toHaveBeenCalledTimes(1);
    */
    // FIXME test state
    // expect((searchBox.state() as State).results).toHaveLength(searchResults.length);
    // expect((searchBox.state() as State).results[0].id).toEqual(searchResults[0].raw.place_id);
    // expect((searchBox.state() as State).attributions).toEqual(licenses);
    // expect(toJson(searchBox)).toMatchSnapshot();
  });

  it('should hide results when query is empty', () => {
    const props: Props = {
      addHandler: jest.fn(),
      boundingBox: null,
      countryCodeSearchFilter: ['XY'],
      searchDelay: 1,
    };

    shallow(<SearchBox {...props} />);
    expect(searchProviderMock().instances).toHaveLength(1);

    // FIXME test state
    /*
    // when there are non-empty results
    searchBox.setState({ results: stateResults, attributions: licenses });
    expect(searchProviderMock().instances).toHaveLength(2);
    expect((searchBox.state() as State).results).toEqual(stateResults);
    expect((searchBox.state() as State).attributions).toEqual(licenses);

    // and an empty query is issued
    const emptyQuery = ' ';
    searchBox.find(SearchInput).simulate('change', emptyQuery);
    expect(searchProviderMock().instances).toHaveLength(3);
    expect(toJson(searchBox)).toMatchSnapshot();

    // search is not invoked
    expect(searchProviderMock().instances[2].search).toHaveBeenCalledTimes(0);
    // and results are cleared
    expect(searchBox.state()).toEqual({ query: emptyQuery, results: [], attributions: [] });
    */
  });

  it('should invoke add handler with the selected result and clear results', () => {
    const mockAddHandler = jest.fn();
    const props: Props = {
      addHandler: mockAddHandler,
      boundingBox: null,
      countryCodeSearchFilter: ['XY'],
      searchDelay: 1,
    };

    shallow(<SearchBox {...props} />);

    /*
    // when there are non-empty results
    searchBox.setState({ results: stateResults, attributions: licenses });
    expect(toJson(searchBox)).toMatchSnapshot();

    const resultItems = searchBox.findWhere(
      (node) => node.key() !== null && node.key().startsWith('place-id-'),
    );
    expect(resultItems).toHaveLength(stateResults.length);

    const selection = Math.floor(stateResults.length / 2);
    resultItems.at(selection).find(Button).simulate('click');
    expect(props.addHandler).toHaveBeenLastCalledWith(stateResults[selection]);

    expect(searchBox.state()).toEqual({ query: '', results: [], attributions: [] });
    */
  });
});

const licenses = ['License 1', 'License 2'];

// FIXME
// eslint-disable-next-line @typescript-eslint/no-unused-vars
const searchResults: SearchResult<RawResult>[] = [{
  label: 'London, ON, Canada',
  x: 101,
  y: 102,
  bounds: [[1, 2], [3, 4]],
  // @ts-expect-error discrepancy between leaflet-geosearch API (expects license) and the actual Nominatim data
  raw: { place_id: 'raw-place-id-1', licence: licenses[0] },
}, {
  label: 'London, OH, USA',
  x: 201,
  y: 202,
  bounds: [[1, 2], [3, 4]],
  // @ts-expect-error discrepancy between leaflet-geosearch API (expects license) and the actual Nominatim data
  raw: { place_id: 'raw-place-id-2', licence: licenses[1] },
}, {
  label: 'London, KY, USA',
  x: 301,
  y: 302,
  bounds: [[1, 2], [3, 4]],
  // @ts-expect-error discrepancy between leaflet-geosearch API (expects license) and the actual Nominatim data
  raw: { place_id: 'raw-place-id-3', licence: licenses[1] },
}, {
  label: 'London, UK',
  x: 401,
  y: 402,
  bounds: [[1, 2], [3, 4]],
  // @ts-expect-error discrepancy between leaflet-geosearch API (expects license) and the actual Nominatim data
  raw: { place_id: 'raw-place-id-4', licence: licenses[0] },
}];

// FIXME
// eslint-disable-next-line @typescript-eslint/no-unused-vars
const stateResults: Result[] = [{
  id: 'place-id-1A',
  address: 'Address 1',
  latLng: { lat: 11, lng: 12 },
}, {
  id: 'place-id-2B',
  address: 'Address 2',
  latLng: { lat: 21, lng: 22 },
}, {
  id: 'place-id-3C',
  address: 'Address 3',
  latLng: { lat: 31, lng: 32 },
}, {
  id: 'place-id-4D',
  address: 'Address 4',
  latLng: { lat: 41, lng: 42 },
}, {
  id: 'place-id-5E',
  address: 'Address 5',
  latLng: { lat: 51, lng: 52 },
}];
