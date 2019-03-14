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
  FormSelect,
  FormSelectOption,
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
import TspMap from 'ui/components/TspMap';

export interface IStateProps {
  depot?: ILocation;
  locations: ILocation[];
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
  isDemoLoading: demo.isLoading,
  locations: routeSelectors.getVisits(plan),
  routes: plan.routes,
});

const mapDispatchToProps: IDispatchProps = {
  addHandler: routeOperations.addLocation,
  clearHandler: routeOperations.clearRoute,
  loadHandler: demoOperations.loadDemo,
  removeHandler: routeOperations.deleteLocation,
};

type IRouteProps = IDispatchProps & IStateProps;

export interface IRouteState {
  center: ILatLng;
  selectedId: number;
  zoom: number;
}

class Route extends React.Component<IRouteProps, IRouteState> {
  constructor(props: IRouteProps) {
    super(props);

    this.state = {
      center: {
        lat: 50.85,
        lng: 4.35,
      },
      selectedId: NaN,
      zoom: 9,
    };
    this.onSelectLocation = this.onSelectLocation.bind(this);
    this.handleMapClick = this.handleMapClick.bind(this);
  }

  handleMapClick(e: any) {
    this.props.addHandler(e.latlng);
  }

  onSelectLocation(id: number) {
    this.setState({ selectedId: id });
  }

  render() {
    const { center, zoom, selectedId } = this.state;
    const {
      depot,
      locations,
      routes,
      removeHandler,
      loadHandler,
      clearHandler,
      isDemoLoading,
    } = this.props;
    return (
      <>
        <TextContent>
          <Text component={TextVariants.h1}>Route</Text>
        </TextContent>
        <Split gutter="md">
          <SplitItem isMain={false}>
            <FormSelect
              value={''}
              // tslint:disable-next-line:no-console
              onChange={e => console.log(e)}
              aria-label="FormSelect Input"
            >
              {[{ disabled: false, value: 'wip', label: 'Vehicle 4' }].map(
                (option, index) => (
                  <FormSelectOption
                    isDisabled={option.disabled}
                    key={index}
                    value={option.value}
                    label={option.label}
                  />
                ),
              )}
            </FormSelect>
            <div
              style={{
                maxHeight: 'calc(100vh - 228px)',
                overflowY: 'auto',
              }}
            >
              <LocationList
                depot={depot}
                locations={locations}
                removeHandler={removeHandler}
                selectHandler={this.onSelectLocation}
                loadHandler={loadHandler}
                clearHandler={clearHandler}
                isDemoLoading={isDemoLoading}
              />
            </div>
          </SplitItem>
          <SplitItem isMain={true}>
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
      </>
    );
  }
}

export default connect(
  mapStateToProps,
  mapDispatchToProps,
)(Route);
