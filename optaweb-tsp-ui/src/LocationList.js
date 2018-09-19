import PropTypes from 'prop-types';
import React from 'react';
import Location from './Location';

function LocationList({ route, domicileId, distance, removeHandler, selectHandler, loadHandler }) {
  return (
    <div className={'leaflet-top leaflet-left leaflet-touch'}>
      <div className={'leaflet-control leaflet-bar w5 bg-white'}>
        {
          route.length === 0 ? (
            <div className={'tc ma2'}>
              <div>Click map to add locations</div>
              <div>or <button style={{ width: '100%' }} onClick={loadHandler}>Load 40 European cities</button></div>
            </div>
          ) : (
            <div>
              <div className={'tl ma2 pa2'}>Distance: {distance}</div>
              <div className={'tl ma2 pa2'}>Locations: {route.length}</div>
              {/*
               The calculated maxHeight is a hack because the constant 116px depends
               on the height of Distance and Locations rows (above) and individual location rows.
               */}
              <div style={{ maxHeight: 'calc(100vh - 116px)', overflowY: 'auto' }}>
                {
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
          )
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
  distance: PropTypes.string.isRequired,
  removeHandler: PropTypes.func.isRequired,
  selectHandler: PropTypes.func.isRequired,
  loadHandler: PropTypes.func.isRequired,
};

export default LocationList;
