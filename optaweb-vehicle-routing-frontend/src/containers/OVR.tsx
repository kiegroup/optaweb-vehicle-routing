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
import { connect } from 'react-redux';
import { Route, RouteComponentProps, withRouter } from 'react-router-dom';
import OVRHeader from '../components/OVRHeader';
import { Depots, Models, Route as RoutePage, Vehicles, Visits } from '../routes';
import { IAppState } from '../store/configStore';
import { demoOperations } from '../store/demo';
import { routeOperations, routeSelectors } from '../store/route';
import { ILatLng, IRouteWithSegments } from '../store/route/types';
import OVRTheme, { OVRThemeConsumer } from '../themes/OVRTheme';
export interface IStateProps {
  route: IRouteWithSegments;
  domicileId: number;
  isDemoLoading: boolean;
}

export interface IDispatchProps {
  removeHandler: typeof routeOperations.deleteLocation;
  loadHandler: typeof demoOperations.loadDemo;
  clearHandler: typeof routeOperations.clearRoute;
  addHandler: typeof routeOperations.addLocation;
}

type Props = RouteComponentProps<any> &
  IStateProps &
  IDispatchProps;

export interface IState {
  center: ILatLng;
  maxDistance: number;
  selectedId: number;
  zoom: number;
}

const mapStateToProps = ({ route, demo }: IAppState): IStateProps => ({
  domicileId: routeSelectors.getDomicileId(route),
  isDemoLoading: demo.isLoading,
  route,
});

const mapDispatchToProps: IDispatchProps = {
  addHandler: routeOperations.addLocation,
  clearHandler: routeOperations.clearRoute,
  loadHandler: demoOperations.loadDemo,
  removeHandler: routeOperations.deleteLocation,
};

class OVR extends React.Component<Props, IState> {
  constructor(props: Props) {
    super(props);
  }
  render() {
    const {
      addHandler,
      route,
      domicileId,
      removeHandler,
      loadHandler,
      clearHandler,
      isDemoLoading,
    } = this.props;
    return (
      <OVRTheme>
        <OVRThemeConsumer>
          {({ components }) => (
            <React.Fragment>
              {components ? components.Background : undefined}
              <Page header={OVRHeader()}>
                <PageSection variant={PageSectionVariants.default}>
                  <Route
                    path="/depots"
                    exact={true}
                    render={() => <Depots />}
                  />
                  <Route
                    path="/models"
                    exact={true}
                    render={() => <Models />}
                  />
                  <Route
                    path="/vehicles"
                    exact={true}
                    render={() => <Vehicles />}
                  />
                  <Route
                    path="/visits"
                    exact={true}
                    render={() => <Visits />}
                  />
                  <Route
                    path="/route"
                    exact={true}
                    render={() => (
                      <RoutePage
                        {...{
                          addHandler,
                          clearHandler,
                          domicileId,
                          isDemoLoading,
                          loadHandler,
                          removeHandler,
                          route,
                        }}
                      />
                    )}
                  />
                </PageSection>
              </Page>
            </React.Fragment>
          )}
        </OVRThemeConsumer>
      </OVRTheme>
    );
  }
}

export default withRouter(
  connect(
    mapStateToProps,
    mapDispatchToProps,
  )(OVR),
);
