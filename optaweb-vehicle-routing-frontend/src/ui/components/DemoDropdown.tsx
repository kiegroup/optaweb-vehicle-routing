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

import { Dropdown, DropdownItem, DropdownPosition, DropdownToggle } from '@patternfly/react-core';
import * as React from 'react';
import './DemoDropdown.css';

export interface Props {
  demos: string[];
  onSelect: (name: string) => void;
}

const dropdownItems = (demos: string[]): React.ReactNode[] => demos.map(value => (
  <DropdownItem key={value}>
    {value}
  </DropdownItem>
));

export const DemoDropdown: React.FC<Props> = ({ demos, onSelect }) => {
  const [isOpen, setOpen] = React.useState(false);
  return (
    <Dropdown
      style={{ marginBottom: 16, marginLeft: 16 }}
      position={DropdownPosition.right}
      isOpen={isOpen}
      dropdownItems={dropdownItems(demos)}
      onSelect={(e) => {
        setOpen(false);
        onSelect(e.currentTarget.innerText);
      }}
      toggle={(
        <DropdownToggle
          disabled={demos.length === 0}
          onToggle={() => setOpen(!isOpen)}
        >
          Load demo...
        </DropdownToggle>
      )}
    />
  );
};
