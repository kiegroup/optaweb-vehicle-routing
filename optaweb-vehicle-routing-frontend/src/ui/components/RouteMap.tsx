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

import * as L from 'leaflet';
import * as React from 'react';
import { Map, Polyline, Rectangle, TileLayer, Viewport, ZoomControl } from 'react-leaflet';
import { LatLng, Location, RouteWithTrack } from 'store/route/types';
import LocationMarker from './LocationMarker';

type Omit<T, K extends keyof T> = Pick<T, Exclude<keyof T, K>>

export interface Props {
  selectedId: number;
  clickHandler: (e: React.SyntheticEvent<HTMLElement>) => void;
  removeHandler: (id: number) => void;
  depot: Location | null;
  visits: Location[];
  routes: Omit<RouteWithTrack, 'vehicle'>[];
  boundingBox: [LatLng, LatLng] | null;
}

export interface State {
  viewport: Viewport;
}

// TODO unlimited unique (random) colors
const colors = ['deepskyblue', 'crimson', 'seagreen', 'slateblue', 'gold', 'darkorange'];

function color(index: number) {
  return colors[index % colors.length];
}

class RouteMap extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = {
      viewport: { center: [0, 0], zoom: 2 },
    };
    this.setViewport = this.setViewport.bind(this);
  }

  componentWillUnmount(): void {
    const { viewport } = this.state;
    console.log(`${viewport.center} [${viewport.zoom}]`);
    // TODO propagate viewport to AppState
  }

  setViewport(viewport: Viewport) {
    this.setState({ viewport });
  }

  render() {
    const {
      boundingBox,
      selectedId,
      depot,
      visits,
      routes,
      clickHandler,
      removeHandler,
    } = this.props;
    const bounds = boundingBox ? new L.LatLngBounds(boundingBox[0], boundingBox[1]) : undefined;
    const { viewport } = this.state;
    return (
      <Map
        bounds={bounds}
        viewport={viewport}
        onViewportChanged={this.setViewport}
        onClick={clickHandler}
        // FIXME use height: 100%
        style={{ width: '100%', height: 'calc(100vh - 176px)' }}
        zoomControl={false} // hide the default zoom control which is on top left
      >
        <TileLayer
          attribution="&copy; <a href='http://osm.org/copyright'>OpenStreetMap</a> contributors"
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        />
        <ZoomControl position="topright" />
        {depot && (
          <LocationMarker
            location={depot}
            isDepot
            isSelected={depot.id === selectedId}
            removeHandler={removeHandler}
          />
        )}
        {visits.map(location => (
          <LocationMarker
            key={location.id}
            location={location}
            isDepot={false}
            isSelected={location.id === selectedId}
            removeHandler={removeHandler}
          />
        ))}
        {routes.map((route, index) => (
          <Polyline
            // eslint-disable-next-line react/no-array-index-key
            key={index} // FIXME use unique id (not iteration index)
            positions={route.track}
            fill={false}
            color={color(index)}
          />
        ))}
        {bounds && (
          <Rectangle
            bounds={bounds}
            color="seagreen"
            fill={false}
            dashArray="10,5"
            weight={1}
          />
        )}
      </Map>
    );
  }
}

export default RouteMap;
