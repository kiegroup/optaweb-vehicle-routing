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
import operations from '../store/operations';
import { ILatLng, ITSPRouteWithSegments } from '../store/tsp/types';
import LocationList from './LocationList';
import TspMap from './TspMap';

interface IStateProps {
  tsp: ITSPRouteWithSegments;
}

interface IDispatchProps {
  removeHandler: typeof operations.deleteLocation;
  loadHandler: typeof operations.loadDemo;
  clearHandler: typeof operations.clearSolution;
  addHandler: typeof operations.addLocation;
}

type Props = IStateProps & IDispatchProps;

interface IState {
  center: ILatLng;
  maxDistance: number;
  selectedId: number;
  zoom: number;
}

const mapStateToProps = ({ route }: IAppState): IStateProps => ({
  tsp: route,
});

const mapDispatchToProps: IDispatchProps = {
  addHandler: operations.addLocation,
  clearHandler: operations.clearSolution,
  loadHandler: operations.loadDemo,
  removeHandler: operations.deleteLocation,
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
    const intDistance = parseInt(this.props.tsp.distance || '0', 10);
    const { maxDistance: currentMax } = this.state;

    if ((currentMax === -1 && intDistance > 0) || currentMax < intDistance) {
      this.setState({ maxDistance: intDistance });
    }
  }

  render() {
    const { center, zoom, selectedId, maxDistance } = this.state;
    const {
      tsp,
      removeHandler,
      loadHandler,
      clearHandler,
    } = this.props;

    return (
      <div>
        <LocationList
          route={tsp}
          maxDistance={maxDistance}
          removeHandler={removeHandler}
          selectHandler={this.onSelectLocation}
          loadHandler={loadHandler}
          clearHandler={clearHandler}
        />
        <TspMap
          center={center}
          zoom={zoom}
          selectedId={selectedId}
          clickHandler={this.handleMapClick}
          removeHandler={removeHandler}
          tsp={tsp}
        />
      </div>
    );
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(TravelingSalesmanProblem);
