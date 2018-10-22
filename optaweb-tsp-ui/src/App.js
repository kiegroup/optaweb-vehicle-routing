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
import SockJS from 'sockjs-client';
import 'tachyons/css/tachyons.css';
import webstomp from 'webstomp-client';
import LocationList from './LocationList';
import TspMap from './TspMap';

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
      route: [],
      domicileId: NaN,
      distance: '',
      stompClient: null,
    };

    this.onClickLoad = this.onClickLoad.bind(this);
    this.onClickMap = this.onClickMap.bind(this);
    this.onClickRemove = this.onClickRemove.bind(this);
    this.onSelectLocation = this.onSelectLocation.bind(this);
  }

  componentDidMount() {
    this.connect('http://localhost:8080/tsp-websocket', () => {
      this.subscribe();
    });
  }

  onClickLoad() {
    this.state.stompClient.send('/app/demo');
  }

  onClickMap(e) {
    console.log(e.latlng);
    this.state.stompClient.send('/app/place', JSON.stringify(e.latlng));
  }

  onClickRemove(id) {
    if (id !== this.state.domicileId || this.state.route.length === 1) {
      this.state.stompClient.send(`/app/place/${id}/delete`);
    }
  }

  onSelectLocation(id) {
    this.setState({ selectedId: id });
  }

  connect(socketUrl, successCallback) {
    const webSocket = new SockJS(socketUrl);
    const stompClient = webstomp.over(webSocket, { debug: true });
    this.setState({ stompClient });
    stompClient.connect(
      {}, // no headers
      () => { // on connection, subscribe to the route topic
        successCallback();
      }, () => { // on error, schedule a reconnection attempt
        setTimeout(() => this.connect(socketUrl, successCallback), 1000);
      });
  }

  subscribe() {
    this.state.stompClient.subscribe('/topic/route', (message) => {
      const tsp = JSON.parse(message.body);
      this.setState({
        route: tsp.route,
        domicileId: tsp.route.length > 0 ? tsp.route[0].id : NaN,
        distance: tsp.distance,
      });
    });
  }

  render() {
    const { center, zoom, selectedId, route, domicileId, distance } = this.state;
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

export default App;
