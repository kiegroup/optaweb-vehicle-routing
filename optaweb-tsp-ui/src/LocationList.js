import PropTypes from 'prop-types';
import React from 'react';
import Location from './Location';

function LocationList({ route, domicileId, removeHandler, selectHandler }) {
  return (
    <div className={'leaflet-top leaflet-left leaflet-touch'}>
      <div className={'leaflet-control leaflet-bar w5 bg-white '}>
        {
          route.length === 0 ?
            <div className={'tc ma2'}>Click map to add locations</div> :
            route
              .slice(0) // clone the array
              .sort((a, b) => a.id - b.id) // because sort is done in place (that would affect the route)
              .map(location => (
                <Location
                  key={location.id}
                  id={location.id}
                  removeDisabled={route.length > 1 && location.id === domicileId}
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
  route: PropTypes.arrayOf(PropTypes.shape({
    id: PropTypes.number.isRequired,
    lat: PropTypes.number.isRequired,
    lng: PropTypes.number.isRequired,
  })).isRequired,
  domicileId: PropTypes.number.isRequired,
  removeHandler: PropTypes.func.isRequired,
  selectHandler: PropTypes.func.isRequired,
};

export default LocationList;
