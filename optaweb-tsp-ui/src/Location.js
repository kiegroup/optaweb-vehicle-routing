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
