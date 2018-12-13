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

import { DataListCell, DataListItem } from '@patternfly/react-core';
import { HomeIcon, MapPinIcon } from '@patternfly/react-icons';
import * as React from 'react';
import LocationContextMenu from './LocationContextMenu';

export interface ILocationProps {
  id: number;
  domicileId: number;
  removeHandler: (id: number) => void;
  selectHandler: (e: any) => void; // FIXME: Event Type
}

const Location: React.SFC<ILocationProps> = ({
  id,
  domicileId,
  removeHandler,
  selectHandler,
}: ILocationProps) => {
  const isDomicile = id === domicileId;
  return (
    <DataListItem aria-labelledby={`check-${id}`} key={id} isExpanded={true}>
      <DataListCell width={1}>
        {isDomicile ? <HomeIcon /> : <MapPinIcon />}
        <span id={`check-${id}`}>Location {id}</span>
      </DataListCell>
      <LocationContextMenu
        removeHandler={event => removeHandler(id)}
        selectHandler={selectHandler}
      />
    </DataListItem>
  );
};

export default Location;
