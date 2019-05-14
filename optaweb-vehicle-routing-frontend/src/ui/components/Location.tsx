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

import { Button, DataListCell, DataListItem, Tooltip } from '@patternfly/react-core';
import { TimesIcon } from '@patternfly/react-icons';
import * as React from 'react';

export interface LocationProps {
  id: number;
  description: string | null;
  removeDisabled: boolean;
  removeHandler: (id: number) => void;
  selectHandler: (id: number) => void;
}

const Location: React.FC<LocationProps> = ({
  id,
  description,
  removeDisabled,
  removeHandler,
  selectHandler,
}) => {
  const [clicked, setClicked] = React.useState(false);

  function shorten(text: string) {
    const first = text.replace(/,.*/, '').trim();
    const short = first.substring(0, Math.min(20, first.length)).trim();
    if (short.length < first.length) {
      return `${short}...`;
    }
    return short;
  }

  return (
    <DataListItem
      isExpanded={false}
      aria-labelledby={`aria-${id}`}
      onMouseEnter={() => selectHandler(id)}
      onMouseLeave={() => selectHandler(NaN)}
    >
      <DataListCell width={4}>
        {description &&
        <Tooltip content={description}>
          <span aria-labelledby={`aria-${id}`}>{shorten(description)}</span>
        </Tooltip>
        ||
        <span aria-labelledby={`aria-${id}`}>{`Location ${id}`}</span>}
      </DataListCell>
      <DataListCell width={1}>
        <Button
          variant="link"
          isDisabled={removeDisabled || clicked}
          onClick={() => {
            setClicked(true);
            removeHandler(id);
          }}
          type="button"
        >
          <TimesIcon />
        </Button>
      </DataListCell>
    </DataListItem>
  );
};

export default Location;
