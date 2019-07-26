/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import { DataList, Text, TextContent, TextVariants } from '@patternfly/react-core';
import * as React from 'react';
import { connect } from 'react-redux';
import { routeSelectors } from 'store/route';
import { Vehicle } from 'store/route/types';
import { AppState } from 'store/types';
import LocationItem from 'ui/components/Location';

export interface StateProps {
  vehicles: Vehicle[];
}

const mapStateToProps = ({ plan }: AppState): StateProps => ({
  vehicles: routeSelectors.getVehicles(plan),
});

export const Vehicles: React.FC<StateProps> = ({ vehicles }) => (
  <>
    <TextContent>
      <Text component={TextVariants.h1}>{`Vehicles (${vehicles.length})`}</Text>
    </TextContent>
    <div style={{ overflowY: 'auto' }}>
      <DataList
        aria-label="simple-item1"
      >
        {vehicles
          .slice(0) // clone the array because
          // sort is done in place (that would affect the route)
          .sort((a, b) => a.id - b.id)
          .map(vehicle => (
            <LocationItem
              key={vehicle.id}
              id={vehicle.id}
              description={vehicle.name}
              removeDisabled={true}
              removeHandler={() => null}
              selectHandler={() => null}
            />
          ))}
      </DataList>
    </div>
  </>
);

export default connect(mapStateToProps)(Vehicles);
