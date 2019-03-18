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
import * as React from 'react';
import { connect } from 'react-redux';
import { IAppState } from 'store/configStore';
import { demoOperations } from 'store/demo';
import { routeOperations, routeSelectors } from 'store/route';
import { ILocation } from 'store/route/types';
import LocationList from 'ui/components/LocationList';

interface IStateProps {
  depot: ILocation | null;
  visits: ILocation[];
  isDemoLoading: boolean;
}

const mapStateToProps = ({ plan, demo }: IAppState): IStateProps => ({
  depot: plan.depot,
  isDemoLoading: demo.isLoading,
  visits: routeSelectors.getVisits(plan),
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

  render() {
    const {
      depot,
      visits,
      removeHandler,
      loadHandler,
      clearHandler,
      isDemoLoading,
    } = this.props;
    return (
      <>
        <TextContent>
          <Text component={TextVariants.h1}>Visits ({visits.length})</Text>
        </TextContent>
        {/* TODO do not show depots */}
        {/* TODO do not show load demo button */}
        <LocationList
          removeHandler={removeHandler}
          selectHandler={() => undefined}
          loadHandler={loadHandler}
          clearHandler={clearHandler}
          depot={depot}
          visits={visits}
          isDemoLoading={isDemoLoading}
        />
      </>
    );
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(Visits);
