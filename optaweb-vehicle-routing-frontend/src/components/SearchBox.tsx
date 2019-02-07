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

import { Button, Grid, GridItem, TextInput } from '@patternfly/react-core';
import { PlusSquareIcon } from '@patternfly/react-icons';
import { OpenStreetMapProvider, SearchResult } from 'leaflet-geosearch';
import * as React from 'react';

interface IProps {
  searchDelay: number;
}

interface IState {
  timeoutId?: number;
  query: string;
  results: string[];
}

class SearchBox extends React.Component<IProps, IState> {

  static defaultProps = {
    searchDelay: 500,
  };

  private provider = new OpenStreetMapProvider({ params: { countrycodes: 'BE' } });

  constructor(props: IProps) {
    super(props);

    this.state = {
      query: '',
      results: [],
    };

    this.handleTextInputChange = this.handleTextInputChange.bind(this);
  }

  handleTextInputChange(query: string): void {
    window.clearTimeout(this.state.timeoutId);
    const timeoutId = window.setTimeout(
      async () => {
        const searchResults: SearchResult[] = await this.provider.search({ query });
        console.log(searchResults);
        this.setState({
          results: searchResults.map(i => i.label),
        });
      },
      this.props.searchDelay);
    this.setState({
      query,
      timeoutId,
    });
  }

  render(): React.ReactNode {
    const { query, results } = this.state;
    return (
      <div>
        <TextInput
          value={query}
          type="search"
          aria-label="geosearch text input"
          onChange={this.handleTextInputChange}
        />

        {results.map((result, index) => (
          <Grid key={index}>
            <GridItem span={11}>{result}</GridItem>
            <GridItem span={1}>
              <Button variant="link" type="button">
                <PlusSquareIcon />
              </Button>
            </GridItem>
          </Grid>
        ))}
      </div>
    );
  }
}

export default SearchBox;
