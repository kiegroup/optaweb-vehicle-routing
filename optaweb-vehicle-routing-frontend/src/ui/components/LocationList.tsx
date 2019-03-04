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

import {
  Button,
  Card,
  CardBody,
  CardHeader,
  DataList,
} from '@patternfly/react-core';
import * as React from 'react';
import { IRoute } from 'store/route/types';
import Location from './Location';

export interface ILocationListProps {
  removeHandler: (id: number) => void;
  selectHandler: (id: number) => void;
  loadHandler: () => void;
  clearHandler: () => void;
  route: IRoute;
  domicileId: number;
  isDemoLoading: boolean;
}

const renderEmptyLocationList = ({
  loadHandler,
  isDemoLoading,
}: ILocationListProps) => {
  return (
    <Card>
      <CardHeader>Click map to add locations</CardHeader>
      <CardBody>
        <Button
          type="button"
          isDisabled={isDemoLoading}
          style={{ width: '100%' }}
          onClick={loadHandler}
        >
          Load demo
        </Button>
      </CardBody>
    </Card>
  );
};

const renderLocationList = ({
  route: { locations },
  domicileId,
  removeHandler,
  selectHandler,
}: ILocationListProps) => {
  return (
    <div style={{ overflowY: 'auto' }}>
      <DataList
        aria-label="simple-item1"
      >
        {locations
          .slice(0) // clone the array because
          // sort is done in place (that would affect the route)
          .sort((a, b) => a.id - b.id)
          .map(location => (
            <Location
              key={location.id}
              id={location.id}
              removeDisabled={locations.length > 1 && location.id === domicileId}
              removeHandler={removeHandler}
              selectHandler={selectHandler}
            />
          ))}
      </DataList>
    </div>
  );
};

const LocationList: React.SFC<ILocationListProps> = (
  props: ILocationListProps,
) => {
  return props.route.locations.length === 0
    ? renderEmptyLocationList(props)
    : renderLocationList(props);
};

export default LocationList;
