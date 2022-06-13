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
