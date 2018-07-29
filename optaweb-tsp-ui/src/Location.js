import PropTypes from 'prop-types';
import React from 'react';

function Location({ id, removeHandler }) {
  return (
    <div
      key={id}
      className={'ma2 flex'}
    >
      <span className={'w-80 pa2'}>{`Location ${id}`}</span>
      <button className={'w-20 pa2'} onClick={() => removeHandler(id)}>x
      </button>
    </div>
  );
}

Location.propTypes = {
  id: PropTypes.number.isRequired,
  removeHandler: PropTypes.func.isRequired,
};

export default Location;
