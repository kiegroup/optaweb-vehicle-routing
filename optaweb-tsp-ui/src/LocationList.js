import PropTypes from 'prop-types';
import React from 'react';
import Location from './Location';

function LocationList({ locations, removeHandler, selectHandler }) {
  return (
    <div className={'leaflet-top leaflet-left leaflet-touch'}>
      <div className={'leaflet-control leaflet-bar w5 bg-white '}>
        {
          locations.length === 0 ?
            <div className={'tc ma2'}>Click map to add locations</div> :
            locations.map(location => (
              <Location
                key={location._links.self.href}
                id={location._links.self.href}
                removeHandler={removeHandler}
                selectHandler={selectHandler}
              />
            ))
        }
      </div>
    </div>
  );
}

LocationList.propTypes = {
  locations: PropTypes.arrayOf(PropTypes.shape({
    lat: PropTypes.number.isRequired,
    lng: PropTypes.number.isRequired,
    _links: PropTypes.object.isRequired,
  })).isRequired,
  removeHandler: PropTypes.func.isRequired,
  selectHandler: PropTypes.func.isRequired,
};

export default LocationList;
