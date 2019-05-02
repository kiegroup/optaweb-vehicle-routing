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

export interface State {
  isOpen: boolean;
}

export class DemoDropdown extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = {
      isOpen: false,
    };
    this.onToggle = this.onToggle.bind(this);
    this.onSelect = this.onSelect.bind(this);
  }

  onToggle(isOpen: boolean) {
    this.setState({
      isOpen,
    });
  }

  onSelect(event: React.SyntheticEvent<HTMLDivElement>) {
    this.setState({
      isOpen: !this.state.isOpen,
    });
    this.props.onSelect(event.currentTarget.innerText);
  }

  dropdownItems(demos: string[]): React.ReactNode[] {
    return demos.map((value, index) => (
      <DropdownItem key={index}>
        {value}
      </DropdownItem>),
    );
  }

  render() {
    const { isOpen } = this.state;
    const { demos } = this.props;
    return (
      <Dropdown
        style={{ marginBottom: 16, marginLeft: 16 }}
        position={DropdownPosition.right}
        isOpen={isOpen}
        dropdownItems={this.dropdownItems(demos)}
        onSelect={this.onSelect}
        toggle={
          <DropdownToggle
            disabled={demos.length === 0}
            onToggle={this.onToggle}
          >
            Load demo...
          </DropdownToggle>}
      />
    );
  }
}
