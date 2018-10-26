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

import React, { Component } from 'react';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';
import 'tachyons/css/tachyons.css';
import LocationList from './LocationList';
import TspMap from './TspMap';
import { tspOperations } from './ducks/tsp/index';

function mapStateToProps(state) {
  return { tsp: state.tsp };
}

function mapDispatchToProps(dispatch) {
  function onClickLoad() {
    dispatch(tspOperations.addLocation(undefined, true));
  }
  function onClickMap(location) {
    dispatch(tspOperations.addLocation(location));
  }

  function onClickRemove(id) {
    dispatch(tspOperations.deleteLocation(id));
  }
  return { onClickLoad, onClickMap, onClickRemove };
}


class App extends Component {
  constructor() {
    super();

    this.state = {
      center: {
        lat: 49.23178,
        lng: 16.57561,
      },
      zoom: 5,
      counter: -1,
      selectedId: NaN,
    };

    this.onClickLoad = this.onClickLoad.bind(this);
    this.onClickMap = this.onClickMap.bind(this);
    this.onClickRemove = this.onClickRemove.bind(this);
    this.onSelectLocation = this.onSelectLocation.bind(this);
  }

  componentDidMount() {

  }

  onClickLoad() {
    this.props.onClickLoad();
  }

  onClickMap(e) {
    this.props.onClickMap(e.latlng);
  }

  onClickRemove(id) {
    if (id !== this.state.domicileId || this.state.route.length === 1) {
      this.props.onClickRemove(id);
    }
  }

  onSelectLocation(id) {
    this.setState({ selectedId: id });
  }

  render() {
    const { center, zoom, selectedId } = this.state;
    const { route, domicileId, distance } = this.props.tsp;
    console.log(`Render, center: ${center}, route: [${route}], selected: ${selectedId}`);

    return (
      <div>
        <LocationList
          route={route}
          domicileId={domicileId}
          distance={distance}
          removeHandler={this.onClickRemove}
          selectHandler={this.onSelectLocation}
          loadHandler={this.onClickLoad}
        />
        <TspMap
          center={center}
          zoom={zoom}
          selectedId={selectedId}
          route={route}
          domicileId={domicileId}
          clickHandler={this.onClickMap}
          removeHandler={this.onClickRemove}
        />
      </div>
    );
  }
}

App.propTypes = {
  tsp: PropTypes.shape({
    route: PropTypes.array.isRequired,
    domicileId: PropTypes.number.isRequired,
    distance: PropTypes.string.isRequired,
  }),
  onClickLoad: PropTypes.func.isRequired,
  onClickMap: PropTypes.func.isRequired,
  onClickRemove: PropTypes.func.isRequired,
};

App.defaultProps = {
  tsp: {
    route: [],
    domicileId: -1,
    distance: 0,
  },
};

if (process.env.NODE_ENV !== 'production' && module.hot) {
  module.hot.accept('./App', App);
}

export default connect(mapStateToProps, mapDispatchToProps)(App);
