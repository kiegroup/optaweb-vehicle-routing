import { Nav, NavItem, NavList } from '@patternfly/react-core';
import * as React from 'react';
import { RouteComponentProps } from 'react-router';
import { Link, withRouter } from 'react-router-dom';
import { pagesByPath } from 'ui/App';

export const Navigation = ({ location }: RouteComponentProps) => (
  <Nav variant="horizontal" aria-label="Nav">
    <NavList>
      {pagesByPath.map(({ path, label }) => (
        <NavItem
          key={path.canonical}
          itemId={path.canonical}
          isActive={[...path.aliases, path.canonical].includes(location.pathname)}
        >
          <Link to={path.canonical}>{label}</Link>
        </NavItem>
      ))}
    </NavList>
  </Nav>
);

export default withRouter(Navigation);
