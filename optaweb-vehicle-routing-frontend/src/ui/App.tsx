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

import { Page, PageSection } from '@patternfly/react-core';
import * as React from 'react';
import { Route, Switch } from 'react-router-dom';
import Alerts from 'ui/components/Alerts';
import './App.css';
import Background from './components/Background';
import { ConnectionManager } from './connection';
import Header from './header/Header';
import { Demo, Route as RoutePage, Vehicles, Visits } from './pages';

export const pagesByPath = [
  { path: { canonical: '/demo', aliases: ['/'] }, page: Demo, label: 'Demo' },
  { path: { canonical: '/vehicles', aliases: [] }, page: Vehicles, label: 'Vehicles' },
  { path: { canonical: '/visits', aliases: [] }, page: Visits, label: 'Visits' },
  { path: { canonical: '/routes', aliases: [] }, page: RoutePage, label: 'Routes' },
];

const App: React.FC = () => (
  <>
    <ConnectionManager />
    <Alerts />
    <Page header={<Header />}>
      <Background />
      <PageSection
        style={{
          display: 'flex',
          flexDirection: 'column',
          overflowY: 'auto',
          height: '100%',
        }}
      >
        <Switch>
          {pagesByPath.map(({ path, page }) => ([path.canonical, ...path.aliases].map((p) => (
            <Route
              key={p}
              path={p}
              exact
              component={page}
            />
          ))))}
        </Switch>
      </PageSection>
    </Page>
  </>
);

export default App;
