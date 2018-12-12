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
import {
  Page,
  PageHeader,
  PageSection,
  PageSectionVariants,
  PageSidebar,
} from '@patternfly/react-core';
import * as React from 'react';
import { ILatLng, ITSPRouteWithSegments } from '../store/tsp/types';
import SolutionExplorer from './SolutionExplorer';
import TspMap from './TspMap';

export interface ITravelingSalesmanProblemProps {
  tsp: ITSPRouteWithSegments;
  removeHandler: (id: number) => void;
  loadHandler: () => void;
  addHandler: (e: React.SyntheticEvent<HTMLElement>) => void;
}

interface ITravelingSalesmanProblemState {
  center: ILatLng;
  maxDistance: number;
  selectedId: number;
  zoom: number;
  isNavOpen: boolean;
}

export default class TravelingSalesmanProblem extends React.Component<
  ITravelingSalesmanProblemProps,
  ITravelingSalesmanProblemState
> {
  static defaultProps = {
    tsp: {
      distance: '0',
      domicileId: -1,
      route: [],
      segments: [],
    },
  };

  constructor(props: ITravelingSalesmanProblemProps) {
    super(props);

    this.state = {
      center: {
        lat: 50.85,
        lng: 4.35,
      },
      isNavOpen: true,
      maxDistance: -1,
      selectedId: NaN,
      zoom: 9,
    };
    this.onSelectLocation = this.onSelectLocation.bind(this);
    this.onNavToggle = this.onNavToggle.bind(this);
    this.onSelectLocation = this.onSelectLocation.bind(this);
    this.renderHeader = this.renderHeader.bind(this);
    this.renderSolutionExplorer = this.renderSolutionExplorer.bind(this);
    this.renderSidebar = this.renderSidebar.bind(this);
  }
  onNavToggle() {
    this.setState({
      isNavOpen: !this.state.isNavOpen,
    });
  }
  onSelectLocation(id: number) {
    this.setState({ selectedId: id });
  }

  componentWillUpdate() {
    const intDistance = parseInt(this.props.tsp.distance || '0', 10);
    const { maxDistance: currentMax } = this.state;

    if ((currentMax === -1 && intDistance > 0) || currentMax < intDistance) {
      this.setState({ maxDistance: intDistance });
    }
  }
  renderHeader() {
    return (
      <PageHeader
        logo="Logo"
        logoProps={{
          href: 'https://patternfly.org',
          // onClick: () => {},
          target: '_blank',
        }}
        toolbar="Toolbar"
        avatar=" | Avatar"
        showNavToggle={true}
        onNavToggle={this.onNavToggle}
      />
    );
  }

  renderSolutionExplorer() {
    const {
      tsp: { route, domicileId, distance },
      removeHandler,
      loadHandler,
    } = this.props;
    const { maxDistance } = this.state;
    return (
      <SolutionExplorer
        route={route}
        domicileId={domicileId}
        distance={distance}
        maxDistance={maxDistance}
        removeHandler={removeHandler}
        selectHandler={this.onSelectLocation}
        loadHandler={loadHandler}
      />
    );
  }
  renderSidebar() {
    const { isNavOpen } = this.state;
    return (
      <PageSidebar nav={this.renderSolutionExplorer()} isNavOpen={isNavOpen} />
    );
  }
  render() {
    const { center, zoom, selectedId } = this.state;
    const {
      tsp: { route, segments, domicileId },
      removeHandler,
      addHandler,
    } = this.props;

    return (
      <React.Fragment>
        <Page header={this.renderHeader()} sidebar={this.renderSidebar()}>
          <PageSection variant={PageSectionVariants.default}>
            <TspMap
              center={center}
              zoom={zoom}
              selectedId={selectedId}
              route={route}
              segments={segments}
              domicileId={domicileId}
              clickHandler={addHandler}
              removeHandler={removeHandler}
            />
          </PageSection>
        </Page>
      </React.Fragment>
    );
  }
}
