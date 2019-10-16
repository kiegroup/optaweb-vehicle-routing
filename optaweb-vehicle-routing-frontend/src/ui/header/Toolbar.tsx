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
  Dropdown,
  DropdownItem,
  DropdownPosition,
  DropdownToggle,
  Toolbar,
  ToolbarGroup,
  ToolbarItem,
} from '@patternfly/react-core';
import { BellIcon, CogIcon } from '@patternfly/react-icons';
import * as React from 'react';

const ToolbarComponent: React.FC = () => {
  const [openTenants, setOpenTenants] = React.useState(false);
  return (
    <Toolbar
      style={{ display: 'none' }} // TODO remove this when we start using tenants
    >
      <ToolbarGroup>
        <ToolbarItem>
          <Dropdown
            isPlain
            position={DropdownPosition.right}
            // eslint-disable-next-line no-console
            onSelect={event => console.log(event)}
            isOpen={openTenants}
            toggle={(
              <DropdownToggle onToggle={() => setOpenTenants(!openTenants)}>
                Tenant Name
              </DropdownToggle>
            )}
            dropdownItems={[
              <DropdownItem key={0}>ACMEE Corp</DropdownItem>,
              <DropdownItem key={1}>Wayne Ent.</DropdownItem>,
            ]}
          />
        </ToolbarItem>
      </ToolbarGroup>
      <ToolbarGroup>
        <ToolbarItem>
          <Button
            id="horizontal-example-uid-01"
            aria-label="Notifications actions"
            variant={ButtonVariant.plain}
          >
            <BellIcon />
          </Button>
        </ToolbarItem>
        <ToolbarItem>
          <Button
            id="horizontal-example-uid-02"
            aria-label="Settings actions"
            variant={ButtonVariant.plain}
          >
            <CogIcon />
          </Button>
        </ToolbarItem>
      </ToolbarGroup>
    </Toolbar>
  );
};

export default ToolbarComponent;
