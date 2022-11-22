import '@patternfly/patternfly/patternfly.css';
import { Button, SearchInput, Text, TextContent, TextVariants } from '@patternfly/react-core';
import { PlusSquareIcon } from '@patternfly/react-icons';
import { OpenStreetMapProvider } from 'leaflet-geosearch';
import { OpenStreetMapProviderOptions } from 'leaflet-geosearch/lib/providers/openStreetMapProvider';
import * as React from 'react';
import { LatLng } from 'store/route/types';
import { BoundingBox } from 'store/server/types';

export interface Result {
  id: string;
  address: string;
  latLng: LatLng;
}

export interface Props {
  searchDelay: number;
  boundingBox: BoundingBox | null;
  countryCodeSearchFilter: string[];
  addHandler: (result: Result) => void;
}

export interface State {
  query: string;
  results: Result[];
  attributions: string[];
}

// Nominatim API: viewbox=<x1>,<y1>,<x2>,<y2> (x is longitude, y is latitude).
type ViewBox = [number, number, number, number];

const viewBox: (bb: BoundingBox) => ViewBox = (bb: BoundingBox) => [bb[0].lng, bb[0].lat, bb[1].lng, bb[1].lat];

const providerOptions = (props: Props): OpenStreetMapProviderOptions => ({
  params: {
    countrycodes: props.countryCodeSearchFilter.toString(),
    viewbox: props.boundingBox ? viewBox(props.boundingBox).toString() : '',
    bounded: !!props.boundingBox,
  },
});

class SearchBox extends React.Component<Props, State> {
  // eslint-disable-next-line max-len
  // https://github.com/airbnb/javascript/blob/eslint-config-airbnb-v18.1.0/packages/eslint-config-airbnb/rules/react.js#L489
  // TODO remove this suppression once the TODO above is resolved:
  // eslint-disable-next-line react/static-property-placement
  static defaultProps: Pick<Props, 'searchDelay'> = {
    searchDelay: 500,
  };

  private searchProvider: OpenStreetMapProvider;

  private timeoutId: number | null;

  constructor(props: Props) {
    super(props);

    this.state = {
      query: '',
      results: [],
      attributions: [],
    };

    this.searchProvider = new OpenStreetMapProvider(providerOptions(props));
    this.timeoutId = null;

    this.handleTextInputChange = this.handleTextInputChange.bind(this);
    this.handleClick = this.handleClick.bind(this);
  }

  componentDidUpdate() {
    this.searchProvider = new OpenStreetMapProvider(providerOptions(this.props));
  }

  componentWillUnmount() {
    if (this.timeoutId) {
      window.clearTimeout(this.timeoutId);
    }
  }

  handleTextInputChange(query: string) {
    if (this.timeoutId) {
      window.clearTimeout(this.timeoutId);
    }
    if (query.trim() !== '') {
      this.timeoutId = window.setTimeout(
        async () => {
          const searchResults = await this.searchProvider.search({ query });
          if (this.state.query !== query) {
            return;
          }
          this.setState({
            results: searchResults
              .map((result) => ({
                id: result.raw.place_id,
                address: result.label,
                latLng: { lat: result.y, lng: result.x },
              })),
            attributions: searchResults
              // eslint-disable-next-line max-len
              // @ts-expect-error discrepancy between leaflet-geosearch API (expects license) and the actual Nominatim data
              .map((result) => result.raw.licence)
              // filter out duplicate elements
              .filter((value, index, array) => array.indexOf(value) === index),
          });
        },
        this.props.searchDelay,
      );
      this.setState({ query });
    } else {
      this.setState({ query, results: [], attributions: [] });
    }
  }

  handleClick(index: number) {
    this.props.addHandler(this.state.results[index]);
    this.setState({
      query: '',
      results: [],
      attributions: [],
    });
    // TODO focus text input
  }

  render() {
    const { attributions, query, results } = this.state;
    return (
      <>
        <SearchInput
          style={{ marginBottom: 10 }}
          value={query}
          placeholder="Search to add a location..."
          aria-label="geosearch text input"
          onChange={this.handleTextInputChange}
          data-cy="geosearch-text-input"
        />
        {results.length > 0 && (
          <div className="pf-c-options-menu pf-m-expanded" style={{ zIndex: 1100 }}>
            <ul className="pf-c-options-menu__menu">
              {results.map((result, index) => (
                <li key={result.id}>
                  <div className="pf-c-options-menu__menu-item">
                    {result.address}
                    <Button
                      className="pf-c-options-menu__menu-item-icon"
                      variant="link"
                      type="button"
                      onClick={() => this.handleClick(index)}
                      data-cy={`geosearch-location-item-button-${index}`}
                    >
                      <PlusSquareIcon />
                    </Button>
                  </div>
                </li>
              ))}

              <li className="pf-c-options-menu__separator" role="separator" />

              {attributions.map((attribution) => (
                <li
                  key={`attrib: ${attribution}`}
                  className="pf-c-options-menu__menu-item pf-m-disabled"
                >
                  <TextContent>
                    <Text
                      component={TextVariants.small}
                    >
                      {attribution}
                    </Text>
                  </TextContent>
                </li>
              ))}
            </ul>
          </div>
        )}
      </>
    );
  }
}

export default SearchBox;
