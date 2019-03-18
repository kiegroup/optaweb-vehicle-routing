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
import { routeOperations } from 'store/route';
import { ILatLng, ILocation, IRouteWithTrack } from 'store/route/types';
import LocationList from 'ui/components/LocationList';
import TspMap from 'ui/components/TspMap';

export interface IStateProps {
  depot: ILocation | null;
  routes: IRouteWithTrack[];
}

export interface IDispatchProps {
  removeHandler: typeof routeOperations.deleteLocation;
  addHandler: typeof routeOperations.addLocation;
}

const mapStateToProps = ({ plan }: IAppState): IStateProps => ({
  depot: plan.depot,
  routes: plan.routes,
});

const mapDispatchToProps: IDispatchProps = {
  addHandler: routeOperations.addLocation,
  removeHandler: routeOperations.deleteLocation,
};

type IRouteProps = IDispatchProps & IStateProps;

export interface IRouteState {
  center: ILatLng;
  selectedId: number;
  selectedRouteId: number;
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
      selectedRouteId: 0,
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
    const { center, zoom, selectedId, selectedRouteId } = this.state;
    const {
      depot,
      routes,
      removeHandler,
    } = this.props;

    // FIXME quick hack to preserve route color by keeping its index
    const filteredRoutes = routes.map((value, index) => {
      return index === selectedRouteId ? value : { visits: [], track: [] };
    });
    const filteredVisits: ILocation[] = routes.length > 0 ? routes[selectedRouteId].visits : [];
    return (
      <>
        <TextContent>
          <Text component={TextVariants.h1}>Route</Text>
        </TextContent>
        <Split gutter="md">
          <SplitItem isMain={false}>
            <FormSelect
              isDisabled={routes.length === 0}
              value={selectedRouteId}
              onChange={(e) => {
                this.setState({ selectedRouteId: parseInt(e as unknown as string, 10) });
              }}
              aria-label="FormSelect Input"
            >
              {routes.map(
                (option, index) => (
                  <FormSelectOption
                    isDisabled={false}
                    key={index}
                    value={index}
                    label={`Route ${index}`}
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
                visits={filteredVisits}
                removeHandler={removeHandler}
                selectHandler={this.onSelectLocation}
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
              routes={filteredRoutes}
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
