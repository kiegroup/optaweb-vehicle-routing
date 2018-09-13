import PropTypes from 'prop-types';
import React from 'react';

function Location({ id, removeDisabled, removeHandler, selectHandler }) {
  return (
    <div
      key={id}
      className={'ma2 flex bg-animate hover-bg-light-gray'}
      onMouseEnter={() => selectHandler(id)}
      onMouseLeave={() => selectHandler(NaN)}
    >
      <span className={'w-80 pa2'}>{`Location ${id}`}</span>
      <button disabled={removeDisabled} className={'w-20 pa2'} onClick={() => removeHandler(id)}>x
      </button>
    </div>
  );
}

Location.propTypes = {
  id: PropTypes.number.isRequired,
  removeDisabled: PropTypes.bool.isRequired,
  removeHandler: PropTypes.func.isRequired,
  selectHandler: PropTypes.func.isRequired,
};

export default Location;
