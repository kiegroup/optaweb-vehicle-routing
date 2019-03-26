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
import './App.css';
import Background from './components/Background';
import ConnectionManager from './connection/ConnectionManager';
import Header from './header/Header';
import { Demo, Route as RoutePage, Vehicles, Visits } from './pages';

const App: React.FC = () => {
  return (
    <>
      <ConnectionManager />
      <Background />
      <Page header={<Header />}>
        <PageSection
          style={{
            display: 'flex',
            flexDirection: 'column',
            overflowY: 'auto',
          }}
        >
          <Switch>
            <Route
              path="/vehicles"
              exact={true}
              component={Vehicles}
            />
            <Route
              path="/visits"
              exact={true}
              component={Visits}
            />
            <Route
              path="/route"
              exact={true}
              component={RoutePage}
            />
            <Route
              path="/demo"
              exact={true}
              component={Demo}
            />
          </Switch>
        </PageSection>
      </Page>
    </>
  );
};

export default App;
