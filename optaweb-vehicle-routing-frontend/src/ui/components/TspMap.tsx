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
import {
  Map,
  Marker,
  Polyline,
  TileLayer,
  Tooltip,
  ZoomControl,
} from 'react-leaflet';
import { ILatLng, IRouteWithTrack } from 'store/route/types';

export interface ITspMapProps {
  center: ILatLng;
  zoom: number;
  selectedId: number;
  clickHandler: (e: React.SyntheticEvent<HTMLElement>) => void;
  removeHandler: (id: number) => void;
  route: IRouteWithTrack;
  domicileId: number;
}

const TspMap: React.FC<ITspMapProps> = ({
  center,
  zoom,
  selectedId,
  route,
  domicileId,
  clickHandler,
  removeHandler,
}) => {
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
  return (
    <Map
      center={center}
      zoom={zoom}
      onClick={clickHandler}
      // FIXME use height: 100%
      style={{ width: '100%', height: 'calc(100vh - 192px)' }}
      zoomControl={false} // hide the default zoom control which is on top left
    >
      <TileLayer
        attribution="&copy; <a href='http://osm.org/copyright'>OpenStreetMap</a> contributors"
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
      />
      <ZoomControl position="topright" />
      {route.visits.map(location => (
        <Marker
          key={location.id}
          position={location}
          icon={location.id === domicileId ? homeIcon : defaultIcon}
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
      ))}
      <Polyline positions={route.track} fill={false} />
    </Map>
  );
};

export default TspMap;
