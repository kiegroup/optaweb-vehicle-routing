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

import { Dropdown, DropdownItem, KebabToggle } from '@patternfly/react-core';
import * as React from 'react';

export interface ILocationContextMenuProps {
  removeHandler: (e: any) => void;
  selectHandler: (e: any) => void;
}
interface ILocationContextMenuState {
  isOpen: boolean;
}

export default class LocationContextMenu extends React.Component<
  ILocationContextMenuProps,
  ILocationContextMenuState
> {
  constructor(props: ILocationContextMenuProps) {
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

  onSelect() {
    this.setState({
      isOpen: !this.state.isOpen,
    });
  }

  render() {
    const { isOpen } = this.state;
    const { removeHandler, selectHandler } = this.props;
    const dropdownItems = [
      // tslint:disable-next-line:jsx-wrap-multiline
      <DropdownItem key="remove" onClick={removeHandler}>
        Remove
      </DropdownItem>,
      // tslint:disable-next-line:jsx-wrap-multiline
      <DropdownItem key="select" onClick={selectHandler}>
        Select
      </DropdownItem>,
    ];

    /*
    uncomment when will be more clear hot wo use with pf-react DataListAction compontents
    return (<DataListAction
      aria-labelledby="check-action-item1 check-action-action1"
      id="check-action-action1"
      aria-label="Actions"
      onClick={() => {
        console.log('xxxxx');
        removeHandler(id);
      }}
    />);*/
    return (
      <Dropdown
        position="right"
        onSelect={this.onSelect}
        toggle={<KebabToggle onToggle={this.onToggle} />}
        isOpen={isOpen}
        dropdownItems={dropdownItems}
      />
    );
  }
}
