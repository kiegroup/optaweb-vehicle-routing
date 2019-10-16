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
  Form,
  FormSelect,
  FormSelectOption,
  GutterSize,
  Split,
  SplitItem,
  Text,
  TextContent,
  TextVariants,
} from '@patternfly/react-core';
import * as React from 'react';
import { connect } from 'react-redux';
import { clientOperations } from 'store/client';
import { UserViewport } from 'store/client/types';
import { routeOperations } from 'store/route';
import { LatLng, Location, RouteWithTrack } from 'store/route/types';
import { AppState } from 'store/types';
import LocationList from 'ui/components/LocationList';
import RouteMap from 'ui/components/RouteMap';

export interface StateProps {
  depot: Location | null;
  visits: Location[];
  routes: RouteWithTrack[];
  boundingBox: [LatLng, LatLng] | null;
  userViewport: UserViewport;
}

export interface DispatchProps {
  addHandler: typeof routeOperations.addLocation;
  removeHandler: typeof routeOperations.deleteLocation;
  updateViewport: typeof clientOperations.updateViewport;
}

const mapStateToProps = ({ plan, serverInfo, userViewport }: AppState): StateProps => ({
  depot: plan.depot,
  visits: plan.visits,
  routes: plan.routes,
  boundingBox: serverInfo.boundingBox,
  userViewport,
});

const mapDispatchToProps: DispatchProps = {
  addHandler: routeOperations.addLocation,
  removeHandler: routeOperations.deleteLocation,
  updateViewport: clientOperations.updateViewport,
};

export type RouteProps = DispatchProps & StateProps;

export interface RouteState {
  selectedId: number;
  selectedRouteId: number;
}

export class Route extends React.Component<RouteProps, RouteState> {
  constructor(props: RouteProps) {
    super(props);

    this.state = {
      selectedId: NaN,
      selectedRouteId: 0,
    };
    this.onSelectLocation = this.onSelectLocation.bind(this);
    this.handleMapClick = this.handleMapClick.bind(this);
  }

  handleMapClick(e: any) {
    this.props.addHandler({ ...e.latlng, description: '' });
  }

  onSelectLocation(id: number) {
    this.setState({ selectedId: id });
  }

  render() {
    const { selectedId, selectedRouteId } = this.state;
    const {
      boundingBox,
      userViewport,
      depot,
      visits,
      routes,
      removeHandler,
      updateViewport,
    } = this.props;

    // FIXME quick hack to preserve route color by keeping its index
    const filteredRoutes = (
      routes.map((value, index) => (index === selectedRouteId ? value : { visits: [], track: [] }))
    );
    const filteredVisits: Location[] = routes.length > 0 ? routes[selectedRouteId].visits : [];
    return (
      <>
        <TextContent>
          <Text component={TextVariants.h1}>Route</Text>
        </TextContent>
        <Split gutter={GutterSize.md}>
          <SplitItem
            isFilled={false}
            style={{ display: 'flex', flexDirection: 'column' }}
          >
            <Form>
              <FormSelect
                style={{ backgroundColor: 'white', marginBottom: 10 }}
                isDisabled={routes.length === 0}
                value={selectedRouteId}
                onChange={(e) => {
                  this.setState({ selectedRouteId: parseInt(e as unknown as string, 10) });
                }}
                aria-label="FormSelect Input"
              >
                {routes.map(
                  (route, index) => (
                    <FormSelectOption
                      isDisabled={false}
                      // eslint-disable-next-line react/no-array-index-key
                      key={index}
                      value={index}
                      label={route.vehicle.name}
                    />
                  ),
                )}
              </FormSelect>
            </Form>
            <LocationList
              depot={depot}
              visits={filteredVisits}
              removeHandler={removeHandler}
              selectHandler={this.onSelectLocation}
            />
          </SplitItem>
          <SplitItem isFilled>
            <RouteMap
              boundingBox={boundingBox}
              userViewport={userViewport}
              updateViewport={updateViewport}
              selectedId={selectedId}
              clickHandler={this.handleMapClick}
              removeHandler={removeHandler}
              depot={depot}
              visits={visits}
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
