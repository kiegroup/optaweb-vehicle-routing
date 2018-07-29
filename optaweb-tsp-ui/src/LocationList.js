import OrderedMap from 'immutable';
import PropTypes from 'prop-types';
import React from 'react';
import Location from './Location';

function LocationList({ locations, removeHandler }) {
  return (
    <div className={'leaflet-top leaflet-left leaflet-touch'}>
      <div className={'leaflet-control leaflet-bar w5 bg-white '}>
        {
          locations.isEmpty() ?
            <div className={'tc ma2'}>Click map to add locations</div> :
            locations.keySeq().map(id => (
              <Location key={id} id={id} removeHandler={removeHandler} />
            ))
        }
      </div>
    </div>
  );
}

LocationList.propTypes = {
  locations: PropTypes.instanceOf(OrderedMap.constructor).isRequired,
  removeHandler: PropTypes.func.isRequired,
};

export default LocationList;
