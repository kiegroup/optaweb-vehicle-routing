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

import { Text, TextContent, TextVariants } from '@patternfly/react-core';
import LocationList from 'components/LocationList';
import * as React from 'react';
import { connect } from 'react-redux';
import { IAppState } from 'store/configStore';
import { demoOperations } from 'store/demo';
import { routeOperations, routeSelectors } from 'store/route';
import { IRoute } from 'store/route/types';

interface IStateProps {
  route: IRoute;
  domicileId: number;
  isDemoLoading: boolean;
}

const mapStateToProps = ({ route, demo }: IAppState): IStateProps => ({
  domicileId: routeSelectors.getDomicileId(route),
  isDemoLoading: demo.isLoading,
  route,
});

export interface IDispatchProps {
  removeHandler: typeof routeOperations.deleteLocation;
  loadHandler: typeof demoOperations.loadDemo;
  clearHandler: typeof routeOperations.clearRoute;
  addHandler: typeof routeOperations.addLocation;
}

const mapDispatchToProps: IDispatchProps = {
  addHandler: routeOperations.addLocation,
  clearHandler: routeOperations.clearRoute,
  loadHandler: demoOperations.loadDemo,
  removeHandler: routeOperations.deleteLocation,
};

type IProps = IStateProps & IDispatchProps;

class Visits extends React.Component<IProps> {

  constructor(props: IProps) {
    super(props);
  }

  render(): React.ReactNode {
    const {
      route,
      domicileId,
      removeHandler,
      loadHandler,
      clearHandler,
      isDemoLoading,
    } = this.props;
    return (
      <React.Fragment>
        <TextContent>
          <Text component={TextVariants.h1}>Visits ({route.locations.length})</Text>
        </TextContent>
        {/* TODO do not show depots */}
        <LocationList
          removeHandler={removeHandler}
          selectHandler={() => undefined}
          loadHandler={loadHandler}
          clearHandler={clearHandler}
          route={route}
          domicileId={domicileId}
          isDemoLoading={isDemoLoading}
        />
      </React.Fragment>
    );
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(Visits);
