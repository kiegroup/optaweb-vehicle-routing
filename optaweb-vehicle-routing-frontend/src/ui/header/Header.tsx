import { Brand, PageHeader } from '@patternfly/react-core';
import * as React from 'react';
import NavigationWithRouter from './Navigation';

const Header: React.FC = () => (
  <PageHeader
    logo={<Brand src="./assets/images/optaPlannerLogoDarkBackground200px.png" alt="OptaPlanner Logo" />}
    topNav={<NavigationWithRouter />}
  />
);

export default Header;
