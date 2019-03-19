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

import { Button, DataListCell, DataListItem } from '@patternfly/react-core';
import { TimesIcon } from '@patternfly/react-icons';
import * as React from 'react';

export interface ILocationProps {
  id: number;
  removeDisabled: boolean;
  removeHandler: (id: number) => void;
  selectHandler: (id: number) => void;
}

const Location: React.FC<ILocationProps> = ({
  id,
  removeDisabled,
  removeHandler,
  selectHandler,
}) => {
  return (
    <DataListItem
      isExpanded={false}
      aria-labelledby={`aria-${id}`}
      onMouseEnter={() => selectHandler(id)}
      onMouseLeave={() => selectHandler(NaN)}
    >
      <DataListCell width={4}>
        <span aria-labelledby={`aria-${id}`}>{`Location ${id}`}</span>
      </DataListCell>
      <DataListCell width={1}>
        <Button
          variant="link"
          isDisabled={removeDisabled}
          onClick={() => removeHandler(id)}
          type="button"
        >
          <TimesIcon />
        </Button>
      </DataListCell>
    </DataListItem>
  );
};

export default Location;
