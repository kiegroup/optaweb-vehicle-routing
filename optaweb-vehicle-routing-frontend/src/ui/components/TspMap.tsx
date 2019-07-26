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
import { Map, Marker, Polyline, Rectangle, TileLayer, Tooltip, ZoomControl } from 'react-leaflet';
import { LatLng, Location, RouteWithTrack } from 'store/route/types';

type Omit<T, K extends keyof T> = Pick<T, Exclude<keyof T, K>>

export interface TspMapProps {
  selectedId: number;
  clickHandler: (e: React.SyntheticEvent<HTMLElement>) => void;
  removeHandler: (id: number) => void;
  depot: Location | null;
  routes: Omit<RouteWithTrack, 'vehicle'>[];
  boundingBox: [LatLng, LatLng] | null;
}

// TODO unlimited unique (random) colors
const colors = ['deepskyblue', 'crimson', 'seagreen', 'slateblue', 'gold', 'darkorange'];

function color(index: number) {
  return colors[index % colors.length];
}

const homeIcon = L.icon({
  iconAnchor: [12, 12],
  iconSize: [24, 24],
  iconUrl: 'if_big_house-home_2222740.png',
  popupAnchor: [0, -10],
  shadowAnchor: [16, 2],
  shadowSize: [50, 16],
  shadowUrl: 'if_big_house-home_2222740_shadow.png',
});

const defaultIcon = new L.Icon.Default();

const marker = (
  removeHandler: (id: number) => void,
  selectedId: number,
  location: Location,
  isDepot: boolean,
) => {
  const icon = isDepot ? homeIcon : defaultIcon;
  return (
    <Marker
      key={location.id}
      position={location}
      icon={icon}
      onClick={() => removeHandler(location.id)}
    >
      <Tooltip
        // The permanent and non-permanent tooltips are different components
        // and need to have different keys
        key={location.id + (location.id === selectedId ? 'T' : 't')}
        permanent={location.id === selectedId}
      >
        {`Location ${location.id} [Lat=${location.lat}, Lng=${location.lng}]`}
      </Tooltip>
    </Marker>
  );
};

const TspMap: React.FC<TspMapProps> = ({
  boundingBox,
  selectedId,
  depot,
  routes,
  clickHandler,
  removeHandler,
}) => {
  const bounds = boundingBox ? new L.LatLngBounds(boundingBox[0], boundingBox[1]) : undefined;
  const center = bounds ? undefined : new L.LatLng(0, 0);
  const zoom = bounds ? undefined : 2;
  return (
    <Map
      bounds={bounds}
      center={center}
      zoom={zoom}
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
      {depot && marker(removeHandler, selectedId, depot, true)}
      {routes.map(route => (
        route.visits.map(location => marker(removeHandler, selectedId, location, false))
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
};

export default TspMap;
