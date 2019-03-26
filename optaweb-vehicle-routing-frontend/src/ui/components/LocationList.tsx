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
import { ILocation } from 'store/route/types';
import Location from './Location';

export interface ILocationListProps {
  removeHandler: (id: number) => void;
  selectHandler: (id: number) => void;
  depot: ILocation | null;
  visits: ILocation[];
}

const renderEmptyLocationList: React.FC<ILocationListProps> = () => {
  return (
    <DataList aria-label="empty location list">
      <Bullseye>No locations</Bullseye>
    </DataList>
  );
};

const renderLocationList: React.FC<ILocationListProps> = ({
  depot,
  visits,
  removeHandler,
  selectHandler,
}) => {
  return (
    <div style={{ overflowY: 'auto' }}>
      <DataList
        aria-label="simple-item1"
      >
        {depot && <Location
          key={depot.id}
          id={depot.id}
          removeDisabled={visits.length > 0}
          removeHandler={removeHandler}
          selectHandler={selectHandler}
        />}
        {visits
          .slice(0) // clone the array because
          // sort is done in place (that would affect the route)
          .sort((a, b) => a.id - b.id)
          .map(visit => (
            <Location
              key={visit.id}
              id={visit.id}
              removeDisabled={false}
              removeHandler={removeHandler}
              selectHandler={selectHandler}
            />
          ))}
      </DataList>
    </div>
  );
};

const LocationList: React.FC<ILocationListProps> = (props) => {
  return props.visits.length === 0 && props.depot === null
    ? renderEmptyLocationList(props)
    : renderLocationList(props);
};

export default LocationList;
