import OrderedMap from 'immutable';
import PropTypes from 'prop-types';
import React from 'react';
import { Map, Marker, Popup, TileLayer, Tooltip, ZoomControl } from 'react-leaflet';

function TspMap({ center, zoom, locations, clickHandler, removeHandler }) {
  return (
    <Map
      center={center}
      zoom={zoom}
      onClick={clickHandler}
      style={{ width: '100vw', height: '100vh' }}
      zoomControl={false} // hide the default zoom control which is on top left
    >
      <TileLayer
        attribution="&amp;copy <a href=&quot;http://osm.org/copyright&quot;>OpenStreetMap</a> contributors"
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
      />
      <ZoomControl position="topright" />
      {
        locations.entrySeq().map(([id, location]) => (
          <Marker
            key={id}
            position={location}
          >
            <Popup>
              <button onClick={() => removeHandler(id)}>x</button>
            </Popup>
            <Tooltip>{`Location ${id}: ${location.toString()}`}</Tooltip>
          </Marker>
        ))
      }
    </Map>
  );
}

TspMap.propTypes = {
  center: PropTypes.shape({
    lat: PropTypes.number.isRequired,
    lng: PropTypes.number.isRequired,
  }).isRequired,
  zoom: PropTypes.number.isRequired,
  locations: PropTypes.instanceOf(OrderedMap.constructor).isRequired,
  clickHandler: PropTypes.func.isRequired,
  removeHandler: PropTypes.func.isRequired,
};

export default TspMap;
