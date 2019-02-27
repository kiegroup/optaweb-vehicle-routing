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

import { Page, PageSection, PageSectionVariants } from '@patternfly/react-core';
import * as React from 'react';
import { Route } from 'react-router-dom';
import ConnectionManager from 'src/containers/ConnectionManager';
import Background from '../components/Background';
import OVRHeader from '../components/OVRHeader';
import { Depots, Models, Route as RoutePage, Vehicles, Visits } from '../routes';
import './App.css';

export default function App() {
  return (
    <React.Fragment>
      <ConnectionManager />
      <Background />
      <Page header={OVRHeader()}>
        <PageSection variant={PageSectionVariants.default}>
          <Route
            path="/depots"
            exact={true}
            component={Depots}
          />
          <Route
            path="/models"
            exact={true}
            component={Models}
          />
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
        </PageSection>
      </Page>
    </React.Fragment>
  );
}
