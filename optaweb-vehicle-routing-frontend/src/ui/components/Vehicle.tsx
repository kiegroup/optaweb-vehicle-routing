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

import {
  Button,
  ButtonVariant,
  DataListCell,
  DataListItem,
  DataListItemRow,
  InputGroup,
  InputGroupText,
} from '@patternfly/react-core';
import { MinusIcon, PlusIcon, TimesIcon } from '@patternfly/react-icons';
import * as React from 'react';
import { VehicleCapacity } from 'store/route/types';

export interface VehicleProps {
  id: number;
  description: string;
  capacity: number;
  removeHandler: (id: number) => void;
  capacityChangeHandler: (vehicleCapacity: VehicleCapacity) => void;
}

const Vehicle: React.FC<VehicleProps> = ({
  id,
  description,
  capacity,
  removeHandler,
  capacityChangeHandler,
}) => {
  const [clicked, setClicked] = React.useState(false);

  return (
    <DataListItem
      isExpanded={false}
      aria-labelledby={`vehicle-${id}`}
    >
      <DataListItemRow>
        <DataListCell isFilled>
          <span id={`vehicle-${id}`}>{description}</span>
        </DataListCell>
        <DataListCell isFilled>
          <InputGroup>
            <Button
              variant={ButtonVariant.primary}
              isDisabled={capacity === 0}
              data-test-key={`capacity-decrease-${id}`}
              onClick={() => capacityChangeHandler({ vehicleId: id, capacity: capacity - 1 })}
            >
              <MinusIcon />
            </Button>
            <InputGroupText readOnly>
              {capacity}
            </InputGroupText>
            <Button
              variant={ButtonVariant.primary}
              data-test-key={`capacity-increase-${id}`}
              onClick={() => capacityChangeHandler({ vehicleId: id, capacity: capacity + 1 })}
            >
              <PlusIcon />
            </Button>
          </InputGroup>
        </DataListCell>
        <DataListCell isFilled={false}>
          <Button
            type="button"
            variant="link"
            data-test-key={`remove-${id}`}
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
