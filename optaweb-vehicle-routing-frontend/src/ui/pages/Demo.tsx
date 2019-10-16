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
  ButtonVariant,
  Grid,
  GridItem,
  GutterSize,
  InputGroup,
  InputGroupText,
  Level,
  LevelItem,
  Split,
  SplitItem,
  Text,
  TextContent,
  TextVariants,
} from '@patternfly/react-core';
import { MinusIcon, PlusIcon } from '@patternfly/react-icons';
import * as React from 'react';
import { connect } from 'react-redux';
import { clientOperations } from 'store/client';
import { UserViewport } from 'store/client/types';
import { demoOperations } from 'store/demo';
import { routeOperations } from 'store/route';
import { LatLng, Location, RouteWithTrack } from 'store/route/types';
import { AppState } from 'store/types';
import { DemoDropdown } from 'ui/components/DemoDropdown';
import LocationList from 'ui/components/LocationList';
import RouteMap from 'ui/components/RouteMap';
import SearchBox, { Result } from 'ui/components/SearchBox';

export const ID_CLEAR_BUTTON = 'clear-button';
export const ID_EXPORT_BUTTON = 'export-button';

export interface StateProps {
  distance: string;
  depot: Location | null;
  vehicleCount: number;
  visits: Location[];
  routes: RouteWithTrack[];
  isDemoLoading: boolean;
  boundingBox: [LatLng, LatLng] | null;
  userViewport: UserViewport;
  countryCodeSearchFilter: string[];
  demoNames: string[];
}

export interface DispatchProps {
  loadHandler: typeof demoOperations.requestDemo;
  clearHandler: typeof routeOperations.clearRoute;
  addLocationHandler: typeof routeOperations.addLocation;
  removeLocationHandler: typeof routeOperations.deleteLocation;
  addVehicleHandler: typeof routeOperations.addVehicle;
  removeVehicleHandler: typeof routeOperations.deleteAnyVehicle;
  updateViewport: typeof clientOperations.updateViewport;
}

const mapStateToProps = ({ plan, demo, serverInfo, userViewport }: AppState): StateProps => ({
  distance: plan.distance,
  vehicleCount: plan.vehicles.length,
  depot: plan.depot,
  visits: plan.visits,
  routes: plan.routes,
  isDemoLoading: demo.isLoading,
  boundingBox: serverInfo.boundingBox,
  userViewport,
  countryCodeSearchFilter: serverInfo.countryCodes,
  // TODO use selector
  // TODO sort demos alphabetically?
  demoNames: (serverInfo.demos && serverInfo.demos.map(value => value.name)) || [],
});

const mapDispatchToProps: DispatchProps = {
  loadHandler: demoOperations.requestDemo,
  clearHandler: routeOperations.clearRoute,
  addLocationHandler: routeOperations.addLocation,
  removeLocationHandler: routeOperations.deleteLocation,
  addVehicleHandler: routeOperations.addVehicle,
  removeVehicleHandler: routeOperations.deleteAnyVehicle,
  updateViewport: clientOperations.updateViewport,
};

export type DemoProps = DispatchProps & StateProps;

export interface DemoState {
  selectedId: number;
}

export class Demo extends React.Component<DemoProps, DemoState> {
  constructor(props: DemoProps) {
    super(props);

    this.state = {
      selectedId: NaN,
    };
    this.handleDemoLoadClick = this.handleDemoLoadClick.bind(this);
    this.handleMapClick = this.handleMapClick.bind(this);
    this.handleSearchResultClick = this.handleSearchResultClick.bind(this);
    this.onSelectLocation = this.onSelectLocation.bind(this);
  }

  handleMapClick(e: any) {
    this.props.addLocationHandler({ ...e.latlng, description: '' }); // TODO use reverse geocoding to find address
  }

  handleSearchResultClick(result: Result) {
    this.props.addLocationHandler({ ...result.latLng, description: result.address });
  }

  handleDemoLoadClick(demoName: string) {
    this.props.loadHandler(demoName);
  }

  onSelectLocation(id: number) {
    this.setState({ selectedId: id });
  }

  render() {
    const { selectedId } = this.state;
    const {
      distance,
      depot,
      vehicleCount,
      visits,
      routes,
      demoNames,
      isDemoLoading,
      boundingBox,
      userViewport,
      countryCodeSearchFilter,
      addVehicleHandler,
      removeVehicleHandler,
      removeLocationHandler,
      clearHandler,
      updateViewport,
    } = this.props;

    const exportDataSet = () => {
      window.open(`${process.env.REACT_APP_BACKEND_URL}/dataset/export`);
    };

    return (
      // FIXME find a way to avoid these style customizations
      <Split gutter={GutterSize.md} style={{ overflowY: 'auto' }}>
        <SplitItem
          isFilled={false}
          style={{ display: 'flex', flexDirection: 'column' }}
        >
          <TextContent>
            <Text component={TextVariants.h1}>Demo</Text>
          </TextContent>
          <SearchBox
            boundingBox={boundingBox}
            countryCodeSearchFilter={countryCodeSearchFilter}
            addHandler={this.handleSearchResultClick}
          />
          <LocationList
            depot={depot}
            visits={visits}
            removeHandler={removeLocationHandler}
            selectHandler={this.onSelectLocation}
          />
        </SplitItem>

        <SplitItem
          isFilled
          style={{ display: 'flex', flexDirection: 'column' }}
        >
          <Split gutter={GutterSize.md}>
            <SplitItem isFilled>
              <Grid>
                <GridItem span={8}>
                  <Level gutter="sm">
                    <LevelItem style={{ display: 'flex' }}>
                      <Level gutter="sm">
                        <LevelItem>
                          <InputGroup>
                            <Button
                              variant={ButtonVariant.primary}
                              isDisabled={vehicleCount === 0}
                              onClick={removeVehicleHandler}
                            >
                              <MinusIcon />
                            </Button>
                            <InputGroupText readOnly>
                              {vehicleCount}
                            </InputGroupText>
                            <Button
                              variant={ButtonVariant.primary}
                              onClick={addVehicleHandler}
                              data-cy="demo-add-vehicle"
                            >
                              <PlusIcon />
                            </Button>
                          </InputGroup>
                        </LevelItem>
                        <LevelItem>vehicles</LevelItem>
                      </Level>
                    </LevelItem>
                    <LevelItem>{`${visits.length} visits`}</LevelItem>
                    <LevelItem>{`Total travel time: ${distance}`}</LevelItem>
                  </Level>
                </GridItem>
                <GridItem span={4} />
              </Grid>
            </SplitItem>
            <SplitItem isFilled={false}>
              <Button
                id={ID_EXPORT_BUTTON}
                isDisabled={!depot || isDemoLoading}
                style={{ marginBottom: 16, marginLeft: 16 }}
                onClick={exportDataSet}
              >
                Export
              </Button>
              {(depot === null && (
                <DemoDropdown
                  demos={demoNames}
                  onSelect={this.handleDemoLoadClick}
                />
              )) || (
                <Button
                  id={ID_CLEAR_BUTTON}
                  isDisabled={isDemoLoading}
                  style={{ marginBottom: 16, marginLeft: 16 }}
                  onClick={clearHandler}
                  data-cy="demo-clear-button"
                >
                  Clear
                </Button>
              )}
            </SplitItem>
          </Split>
          <RouteMap
            boundingBox={boundingBox}
            userViewport={userViewport}
            updateViewport={updateViewport}
            selectedId={selectedId}
            clickHandler={this.handleMapClick}
            removeHandler={removeLocationHandler}
            depot={depot}
            routes={routes}
            visits={visits}
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
