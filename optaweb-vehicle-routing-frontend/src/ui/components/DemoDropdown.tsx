import { Dropdown, DropdownItem, DropdownPosition, DropdownToggle } from '@patternfly/react-core';
import * as React from 'react';
import './DemoDropdown.css';

export interface Props {
  demos: string[];
  onSelect: (name: string) => void;
}

const dropdownItems = (demos: string[]): React.ReactNode[] => demos.map((value) => (
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
        if (e && e.currentTarget) {
          onSelect(e.currentTarget.innerText);
        }
      }}
      toggle={(
        <DropdownToggle
          isPrimary
          disabled={demos.length === 0}
          onToggle={() => setOpen(!isOpen)}
        >
          Load demo
        </DropdownToggle>
      )}
    />
  );
};
