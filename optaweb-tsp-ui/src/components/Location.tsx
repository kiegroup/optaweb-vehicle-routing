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

import { Button, Grid, GridItem } from '@patternfly/react-core';
import { TimesIcon } from '@patternfly/react-icons';
import * as React from 'react';

export interface ILocationProps {
  id: number;
  removeDisabled: boolean;
  removeHandler: (id: number) => void;
  selectHandler: (e: any) => void; // FIXME: Event Type
}

const Location: React.SFC<ILocationProps> = ({
  id,
  removeDisabled,
  removeHandler,
  selectHandler,
}: ILocationProps) => {
  return (
    <Grid gutter="md">
      <GridItem
        key={id}
        onMouseEnter={() => selectHandler(id)}
        onMouseLeave={() => selectHandler(NaN)}
        span={9}
      >
        {`Location ${id}`}
      </GridItem>
      <GridItem span={3}>
        <Button
          variant="link"
          isDisabled={removeDisabled}
          onClick={() => removeHandler(id)}
          type="button"
        >
          <TimesIcon />
        </Button>
      </GridItem>
    </Grid>
  );
};

export default Location;
