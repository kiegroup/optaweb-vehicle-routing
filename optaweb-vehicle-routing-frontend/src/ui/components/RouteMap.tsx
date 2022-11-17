import * as L from 'leaflet';
import * as React from 'react';
import { Map, Polyline, Rectangle, TileLayer, ZoomControl } from 'react-leaflet';
import { UserViewport } from 'store/client/types';
import { Location, RouteWithTrack } from 'store/route/types';
import { BoundingBox } from 'store/server/types';
import LocationMarker from './LocationMarker';

type Omit<T, K extends keyof T> = Pick<T, Exclude<keyof T, K>>;

export interface Props {
  selectedId: number;
  clickHandler: (event: L.LeafletMouseEvent) => void;
  removeHandler: (id: number) => void;
  depot: Location | null;
  visits: Location[];
  routes: Omit<RouteWithTrack, 'vehicle'>[];
  boundingBox: BoundingBox | null;
  userViewport: UserViewport;
  updateViewport: (viewport: UserViewport) => void;
}

// TODO unlimited unique (random) colors
const colors = ['deepskyblue', 'crimson', 'seagreen', 'slateblue', 'gold', 'darkorange'];

function color(index: number) {
  return colors[index % colors.length];
}

const RouteMap: React.FC<Props> = ({
  boundingBox,
  userViewport,
  selectedId,
  depot,
  visits,
  routes,
  clickHandler,
  removeHandler,
  updateViewport,
}) => {
  const bounds = boundingBox ? new L.LatLngBounds(boundingBox[0], boundingBox[1]) : undefined;
  // do not use bounds if user's viewport is dirty
  const mapBounds = userViewport.isDirty ? undefined : bounds;
  // TODO make TileLayer URL configurable
  // @ts-expect-error Cypress exists on window during Cypress test runs
  const tileLayerUrl = window.Cypress ? 'test-mode-empty-url' : 'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png';
  return (
    <Map
      bounds={mapBounds}
      viewport={userViewport}
      onViewportChanged={updateViewport}
      onClick={clickHandler}
      // FIXME use height: 100%
      style={{ width: '100%', height: 'calc(100vh - 176px)' }}
      zoomControl={false} // hide the default zoom control which is on top left
    >
      <TileLayer
        attribution="&copy; <a href='http://osm.org/copyright'>OpenStreetMap</a> contributors"
        url={tileLayerUrl}
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
      {visits.map((location) => (
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
          smoothFactor={3}
          weight={9}
          opacity={0.6666}
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

export default RouteMap;
