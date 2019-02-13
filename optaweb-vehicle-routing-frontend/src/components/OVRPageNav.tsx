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

import { Nav, NavItem, NavList, NavVariants } from '@patternfly/react-core';
import * as React from 'react';
import { Link } from 'react-router-dom';

const PageNav = () => {
  return (
    <Nav aria-label="Nav">
      <NavList variant={NavVariants.horizontal}>
        <NavItem itemId={'/depots'}>
          <Link to="/depots">Depots</Link>
        </NavItem>
        <NavItem itemId={'models'}>
          <Link to="/models">Models</Link>
        </NavItem>
        <NavItem itemId={'vehicles'}>
          <Link to="/vehicles">Vehicles</Link>
        </NavItem>
        <NavItem itemId={'visits'}>
          <Link to="/visits">Visits</Link>
        </NavItem>
        <NavItem itemId={'route'}>
          <Link to="/route">Route</Link>
        </NavItem>
      </NavList>
    </Nav>
  );
};

export default PageNav;
