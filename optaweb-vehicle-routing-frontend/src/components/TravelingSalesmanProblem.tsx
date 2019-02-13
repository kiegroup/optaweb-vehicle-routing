/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
import * as React from 'react';
import { connect } from 'react-redux';
import { IAppState } from '../store/configStore';
import { demoOperations } from '../store/demo';
import { routeOperations, routeSelectors } from '../store/route';
import { ILatLng, IRouteWithSegments } from '../store/route/types';
import LocationList from './LocationList';
import TspMap from './TspMap';

interface IStateProps {
  route: IRouteWithSegments;
  domicileId: number;
  isDemoLoading: boolean;
}

interface IDispatchProps {
  removeHandler: typeof routeOperations.deleteLocation;
  loadHandler: typeof demoOperations.loadDemo;
  clearHandler: typeof routeOperations.clearRoute;
  addHandler: typeof routeOperations.addLocation;
}

type Props = IStateProps & IDispatchProps;

interface IState {
  center: ILatLng;
  maxDistance: number;
  selectedId: number;
  zoom: number;
}

const mapStateToProps = ({ route, demo }: IAppState): IStateProps => ({
  domicileId: routeSelectors.getDomicileId(route),
  isDemoLoading: demo.isLoading,
  route,
});

const mapDispatchToProps: IDispatchProps = {
  addHandler: routeOperations.addLocation,
  clearHandler: routeOperations.clearRoute,
  loadHandler: demoOperations.loadDemo,
  removeHandler: routeOperations.deleteLocation,
};

class TravelingSalesmanProblem extends React.Component<Props, IState> {
  constructor(props: Props) {
    super(props);

    this.state = {
      center: {
        lat: 50.85,
        lng: 4.35,
      },
      maxDistance: -1,
      selectedId: NaN,
      zoom: 9,
    };
    this.onSelectLocation = this.onSelectLocation.bind(this);
    this.handleMapClick = this.handleMapClick.bind(this);
  }

  onSelectLocation(id: number) {
    this.setState({ selectedId: id });
  }

  handleMapClick(e: any) {
    this.props.addHandler(e.latlng);
  }

  componentWillUpdate() {
    const intDistance = parseInt(this.props.route.distance || '0', 10);
    const { maxDistance: currentMax } = this.state;

    if ((currentMax === -1 && intDistance > 0) || currentMax < intDistance) {
      this.setState({ maxDistance: intDistance });
    }
  }

  render() {
    const { center, zoom, selectedId, maxDistance } = this.state;
    const {
      route,
      domicileId,
      removeHandler,
      loadHandler,
      clearHandler,
      isDemoLoading,
    } = this.props;

    return (
      <div>
        <LocationList
          route={route}
          domicileId={domicileId}
          maxDistance={maxDistance}
          removeHandler={removeHandler}
          selectHandler={this.onSelectLocation}
          loadHandler={loadHandler}
          clearHandler={clearHandler}
          isDemoLoading={isDemoLoading}
        />
        <TspMap
          center={center}
          zoom={zoom}
          selectedId={selectedId}
          clickHandler={this.handleMapClick}
          removeHandler={removeHandler}
          route={route}
          domicileId={domicileId}
        />
      </div>
    );
  }
}

export default connect(
  mapStateToProps,
  mapDispatchToProps,
)(TravelingSalesmanProblem);
