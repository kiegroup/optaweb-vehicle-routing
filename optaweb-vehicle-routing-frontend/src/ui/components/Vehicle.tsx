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

import { Button, DataListCell, DataListItem, DataListItemRow } from '@patternfly/react-core';
import { TimesIcon } from '@patternfly/react-icons';
import * as React from 'react';

export interface VehicleProps {
  id: number;
  description: string;
  capacity: number;
  removeHandler: (id: number) => void;
}

const Vehicle: React.FC<VehicleProps> = ({
  id,
  description,
  capacity,
  removeHandler,
}) => {
  const [clicked, setClicked] = React.useState(false);

  return (
    <DataListItem
      isExpanded={false}
      aria-labelledby={`vehicle-${id}`}
    >
      <DataListItemRow>
        <DataListCell isFilled={true}>
          <span id={`vehicle-${id}`}>{description}</span>
        </DataListCell>
        <DataListCell isFilled={true}>
          {capacity}
        </DataListCell>
        <DataListCell isFilled={false}>
          <Button
            type="button"
            variant="link"
            isDisabled={clicked}
            onClick={() => {
              setClicked(true);
              removeHandler(id);
            }}
          >
            <TimesIcon />
          </Button>
        </DataListCell>
      </DataListItemRow>
    </DataListItem>
  );
};

export default Vehicle;
