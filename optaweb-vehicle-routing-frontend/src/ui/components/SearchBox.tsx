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

import '@patternfly/patternfly/patternfly.css';
import { Button, Text, TextContent, TextInput, TextVariants } from '@patternfly/react-core';
import { PlusSquareIcon } from '@patternfly/react-icons';
import { OpenStreetMapProvider } from 'leaflet-geosearch';
import * as React from 'react';
import { ILatLng } from 'store/route/types';

export interface IProps {
  searchDelay: number;
  addHandler: (result: IResult) => void;
}

export interface IState {
  query: string;
  results: IResult[];
  attributions: string[];
}

export interface IResult {
  address: string;
  latLng: ILatLng;
}

class SearchBox extends React.Component<IProps, IState> {

  static defaultProps = {
    searchDelay: 500,
  };

  private searchProvider = new OpenStreetMapProvider({ params: { countrycodes: 'BE' } });
  private timeoutId: number;

  constructor(props: IProps) {
    super(props);

    this.state = {
      attributions: [],
      query: '',
      results: [],
    };

    this.handleTextInputChange = this.handleTextInputChange.bind(this);
    this.handleClick = this.handleClick.bind(this);
  }

  componentWillUnmount() {
    window.clearTimeout(this.timeoutId);
  }

  handleTextInputChange(query: string): void {
    window.clearTimeout(this.timeoutId);
    if (query.trim() !== '') {
      this.timeoutId = window.setTimeout(
        async () => {
          const searchResults = await this.searchProvider.search({ query });
          if (this.state.query !== query) {
            return;
          }
          this.setState({
            attributions: searchResults
              .map(result => result.raw.licence)
              // filter out duplicate elements
              .filter((value, index, array) => array.indexOf(value) === index),
            results: searchResults.map(result => ({
              address: result.label,
              latLng: { lat: result.y, lng: result.x },
            })),
          });
        },
        this.props.searchDelay);
    } else {
      this.setState({ results: [], attributions: [] });
    }
    this.setState({ query });
  }

  handleClick(index: number) {
    this.props.addHandler(this.state.results[index]);
    this.setState({
      attributions: [],
      query: '',
      results: [],
    });
  }

  render() {
    const { attributions, query, results } = this.state;
    return (
      <>
        <TextInput
          style={{ marginBottom: 10 }}
          value={query}
          type="search"
          placeholder={'Search to add a location...'}
          aria-label="geosearch text input"
          onChange={this.handleTextInputChange}
        />
        {results.length > 0 &&
        <div className="pf-c-options-menu pf-m-expanded" style={{ zIndex: 1100 }}>
          <ul className="pf-c-options-menu__menu">
            {results.map((result, index) => (
              <li key={`result${index}`}>
                <div className="pf-c-options-menu__menu-item">
                  {result.address}
                  <Button
                    className="pf-c-options-menu__menu-item-icon"
                    variant="link"
                    type="button"
                    onClick={() => this.handleClick(index)}
                  >
                    <PlusSquareIcon />
                  </Button>
                </div>
              </li>
            ))}

            <li className="pf-c-options-menu__separator" role="separator" />

            {attributions.map((attribution, index) => (
              <li
                key={`attrib${index}`}
                className="pf-c-options-menu__menu-item pf-m-disabled"
              >
                <TextContent>
                  <Text
                    key={index}
                    component={TextVariants.small}
                  >
                    {attribution}
                  </Text>
                </TextContent>
              </li>
            ))}
          </ul>
        </div>
        }
      </>
    );
  }
}

export default SearchBox;
