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

import { Button, Card, CardBody, CardHeader } from '@patternfly/react-core';
import * as React from 'react';
import { ITSPRoute } from '../store/tsp/types';
import LocationList from './LocationList';
import TripData from './TripData';

export interface ISolutionExplorerProps extends ITSPRoute {
  removeHandler: (id: number) => void;
  selectHandler: (e: any) => void;
  loadHandler: () => void;
  maxDistance: number;
}

const renderEmptySolutionExplorer = ({
  loadHandler,
}: ISolutionExplorerProps) => {
  return (
    <Card>
      <CardHeader>Click map to add locations</CardHeader>
      <CardBody>
        <Button type="button" style={{ width: '100%' }} onClick={loadHandler}>
          Load 40 European cities
        </Button>
      </CardBody>
    </Card>
  );
};

const renderSolutionExplorer = ({
  route,
  domicileId,
  distance = '',
  removeHandler,
  selectHandler,
  loadHandler,
  maxDistance,
}: ISolutionExplorerProps) => {
  return (
    <React.Fragment>
      <br />
      Locations: {route.length}
      <TripData
        maxDistance={maxDistance}
        distance={parseInt(distance, 10) || maxDistance}
      />
      <hr />
      {route.length === 0 ? (
        <Card>
          <CardHeader>Click map to add locations</CardHeader>
          <CardBody>
            <Button
              type="button"
              style={{ width: '100%' }}
              onClick={loadHandler}
            >
              Load 40 European cities
            </Button>
          </CardBody>
        </Card>
      ) : (
        <LocationList
          {...{ route, domicileId, removeHandler, selectHandler }}
        />
      )}
    </React.Fragment>
  );
};

const SolutionExplorer: React.SFC<ISolutionExplorerProps> = (
  props: ISolutionExplorerProps,
) =>
  props.route.length === 0
    ? renderEmptySolutionExplorer(props)
    : renderSolutionExplorer(props);

export default SolutionExplorer;
