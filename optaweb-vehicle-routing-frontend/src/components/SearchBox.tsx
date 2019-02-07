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

import { TextInput } from '@patternfly/react-core';
import { OpenStreetMapProvider, SearchResult } from 'leaflet-geosearch';
import * as React from 'react';

interface IProps {
  searchDelay: number;
}

interface IState {
  query: string;
  timeoutId?: number;
}

class SearchBox extends React.Component<IProps, IState> {

  static defaultProps = {
    searchDelay: 500,
  };

  private provider = new OpenStreetMapProvider();

  constructor(props: IProps) {
    super(props);

    this.state = { query: '' };

    this.handleTextInputChange = this.handleTextInputChange.bind(this);
  }

  handleTextInputChange(query: string): void {
    window.clearTimeout(this.state.timeoutId);
    const timeoutId = window.setTimeout(
      async () => {
        const results: SearchResult[] = await this.provider.search({ query });
        console.log(results);
      },
      this.props.searchDelay);
    this.setState({ query, timeoutId });
  }

  render(): React.ReactNode {
    const { query } = this.state;
    return (
      <TextInput
        value={query}
        type="search"
        onChange={this.handleTextInputChange}
        aria-label="geosearch text input"
      />
    );
  }
}

export default SearchBox;
