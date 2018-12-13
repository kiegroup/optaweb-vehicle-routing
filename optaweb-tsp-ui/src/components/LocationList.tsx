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

import { DataList } from '@patternfly/react-core';
import * as React from 'react';
import { ITSPRoute } from '../store/tsp/types';
import Location from './Location';

export interface ILocationListProps extends ITSPRoute {
  removeHandler: (id: number) => void;
  selectHandler: (e: any) => void;
}

const LocationList: React.SFC<ILocationListProps> = ({
  route,
  domicileId,
  removeHandler,
  selectHandler,
}: ILocationListProps) => {
  return (
    <DataList aria-label="Ciao">
      {[...route] // clone the array because
        // sort is done in place (that would affect the route)
        .sort((a, b) => a.id - b.id)
        .map(location => (
          <Location
            key={`location-${location.id}`}
            {...{ id: location.id, removeHandler, selectHandler, domicileId }}
          />
        ))}
    </DataList>
  );
};

export default LocationList;
