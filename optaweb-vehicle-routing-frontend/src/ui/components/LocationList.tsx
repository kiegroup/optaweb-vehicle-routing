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

import { Bullseye, DataList } from '@patternfly/react-core';
import * as React from 'react';
import { Location } from 'store/route/types';
import LocationItem from './Location';
import './LocationList.css';

export interface LocationListProps {
  removeHandler: (id: number) => void;
  selectHandler: (id: number) => void;
  depot: Location | null;
  visits: Location[];
}

const renderEmptyLocationList: React.FC<LocationListProps> = () => (
  <DataList aria-label="Empty location list">
    <Bullseye>No locations</Bullseye>
  </DataList>
);

const renderLocationList: React.FC<LocationListProps> = ({
  depot,
  visits,
  removeHandler,
  selectHandler,
}) => (
  <div style={{ overflowY: 'auto' }} data-cy="location-list">
    <DataList
      aria-label="List of locations"
    >
      {depot && (
        <LocationItem
          key={depot.id}
          id={depot.id}
          description={depot.description || null}
          removeDisabled={visits.length > 0}
          removeHandler={removeHandler}
          selectHandler={selectHandler}
        />
      )}
      {visits
        .slice(0) // clone the array because
        // sort is done in place (that would affect the route)
        .sort((a, b) => a.id - b.id)
        .map(visit => (
          <LocationItem
            key={visit.id}
            id={visit.id}
            description={visit.description || null}
            removeDisabled={false}
            removeHandler={removeHandler}
            selectHandler={selectHandler}
          />
        ))}
    </DataList>
  </div>
);

const LocationList: React.FC<LocationListProps> = props => (
  props.visits.length === 0 && props.depot === null
    ? renderEmptyLocationList(props)
    : renderLocationList(props)
);

export default LocationList;
