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

import {
  Button,
  Grid,
  GridItem,
  Split,
  SplitItem,
  Text,
  TextContent,
  TextVariants,
} from '@patternfly/react-core';
import * as React from 'react';
import { connect } from 'react-redux';
import { IAppState } from 'store/configStore';
import { demoOperations } from 'store/demo';
import { routeOperations, routeSelectors } from 'store/route';
import { ILatLng, ILocation, IRouteWithTrack } from 'store/route/types';
import LocationList from 'ui/components/LocationList';
import SearchBox, { IResult } from 'ui/components/SearchBox';
import TspMap from 'ui/components/TspMap';

export interface IStateProps {
  distance: string;
  depot: ILocation | null;
  visits: ILocation[];
  routes: IRouteWithTrack[];
  isDemoLoading: boolean;
}

export interface IDispatchProps {
  removeHandler: typeof routeOperations.deleteLocation;
  loadHandler: typeof demoOperations.loadDemo;
  clearHandler: typeof routeOperations.clearRoute;
  addHandler: typeof routeOperations.addLocation;
}

const mapStateToProps = ({ plan, demo }: IAppState): IStateProps => ({
  depot: plan.depot,
  distance: plan.distance,
  isDemoLoading: demo.isLoading,
  routes: plan.routes,
  visits: routeSelectors.getVisits(plan),
});

const mapDispatchToProps: IDispatchProps = {
  addHandler: routeOperations.addLocation,
  clearHandler: routeOperations.clearRoute,
  loadHandler: demoOperations.loadDemo,
  removeHandler: routeOperations.deleteLocation,
};

export type IDemoProps = IDispatchProps & IStateProps;

export interface IDemoState {
  center: ILatLng;
  selectedId: number;
  zoom: number;
}

export class Demo extends React.Component<IDemoProps, IDemoState> {
  constructor(props: IDemoProps) {
    super(props);

    this.state = {
      center: {
        lat: 50.85,
        lng: 4.35,
      },
      selectedId: NaN,
      zoom: 9,
    };
    this.handleMapClick = this.handleMapClick.bind(this);
    this.handleSearchResultClick = this.handleSearchResultClick.bind(this);
    this.onSelectLocation = this.onSelectLocation.bind(this);
  }

  handleMapClick(e: any) {
    this.props.addHandler(e.latlng);
  }

  handleSearchResultClick(result: IResult) {
    this.props.addHandler(result.latLng);
  }

  onSelectLocation(id: number) {
    this.setState({ selectedId: id });
  }

  render() {
    const { center, zoom, selectedId } = this.state;
    const {
      distance,
      depot,
      visits,
      routes,
      removeHandler,
      loadHandler,
      clearHandler,
      isDemoLoading,
    } = this.props;
    return (
      // FIXME find a way to avoid these style customizations
      <Split gutter="md" style={{ overflowY: 'auto' }}>
        <SplitItem
          isMain={false}
          style={{ display: 'flex', flexDirection: 'column' }}
        >
          <TextContent>
            <Text component={TextVariants.h1}>Demo</Text>
          </TextContent>
          <SearchBox addHandler={this.handleSearchResultClick} />
          <LocationList
            depot={depot}
            visits={visits}
            removeHandler={removeHandler}
            selectHandler={this.onSelectLocation}
          />
        </SplitItem>

        <SplitItem
          isMain={true}
          style={{ display: 'flex', flexDirection: 'column' }}
        >
          <Split gutter="md">
            <SplitItem isMain={true}>
              <Grid>
                <GridItem span={6}>{`Visits: ${visits.length}`}</GridItem>
                <GridItem span={6}>{`Distance: ${distance}`}</GridItem>
              </Grid>
            </SplitItem>
            <SplitItem isMain={false}>
              {routes.length === 0 &&
              <Button
                type="button"
                isDisabled={isDemoLoading}
                style={{ marginBottom: 16 }}
                onClick={loadHandler}
              >
                Load demo
              </Button>
              ||
              <Button
                type="button"
                isDisabled={isDemoLoading}
                style={{ marginBottom: 16 }}
                onClick={clearHandler}
              >
                Clear
              </Button>
              }
            </SplitItem>
          </Split>
          <TspMap
            center={center}
            zoom={zoom}
            selectedId={selectedId}
            clickHandler={this.handleMapClick}
            removeHandler={removeHandler}
            depot={depot}
            routes={routes}
          />
        </SplitItem>
      </Split>
    );
  }
}

export default connect(
  mapStateToProps,
  mapDispatchToProps,
)(Demo);
