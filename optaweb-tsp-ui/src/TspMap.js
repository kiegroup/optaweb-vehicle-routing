import PropTypes from 'prop-types';
import React from 'react';
import { Map, Marker, Popup, TileLayer, Tooltip, ZoomControl } from 'react-leaflet';

function TspMap({ center, zoom, locations, selectedId, clickHandler, removeHandler }) {
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
        locations.map(location => (
          <Marker
            key={location._links.self.href}
            position={location}
          >
            <Popup>
              <button onClick={() => removeHandler(location._links.self.href)}>x</button>
            </Popup>
            <Tooltip
              // The permanent and non-permanent tooltips are different components
              // and need to have different keys
              key={location._links.self.href + (location._links.self.href === selectedId ? 'T' : 't')}
              permanent={location._links.self.href === selectedId}
            >
              {`Location ${location._links.self.href.replace(/.*\//, '')} [Lat=${location.lat}, Lng=${location.lng}]`}
            </Tooltip>
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
  locations: PropTypes.arrayOf(PropTypes.shape({
    lat: PropTypes.number.isRequired,
    lng: PropTypes.number.isRequired,
    _links: PropTypes.object.isRequired,
  })).isRequired,
  selectedId: PropTypes.string.isRequired,
  clickHandler: PropTypes.func.isRequired,
  removeHandler: PropTypes.func.isRequired,
};

export default TspMap;
