import {
  Form,
  FormSelect,
  FormSelectOption,
  Split,
  SplitItem,
  Title,
} from '@patternfly/react-core';
import { LeafletMouseEvent } from 'leaflet';
import * as React from 'react';
import { connect } from 'react-redux';
import { clientOperations } from 'store/client';
import { UserViewport } from 'store/client/types';
import { routeOperations } from 'store/route';
import { Location, RouteWithTrack } from 'store/route/types';
import { BoundingBox } from 'store/server/types';
import { AppState } from 'store/types';
import LocationList from 'ui/components/LocationList';
import RouteMap from 'ui/components/RouteMap';
import { sideBarStyle } from 'ui/pages/common';

export interface StateProps {
  depot: Location | null;
  visits: Location[];
  routes: RouteWithTrack[];
  boundingBox: BoundingBox | null;
  userViewport: UserViewport;
}

export interface DispatchProps {
  addHandler: typeof routeOperations.addLocation;
  removeHandler: typeof routeOperations.deleteLocation;
  updateViewport: typeof clientOperations.updateViewport;
}

const mapStateToProps = ({ plan, serverInfo, userViewport }: AppState): StateProps => ({
  depot: plan.depot,
  visits: plan.visits,
  routes: plan.routes,
  boundingBox: serverInfo.boundingBox,
  userViewport,
});

const mapDispatchToProps: DispatchProps = {
  addHandler: routeOperations.addLocation,
  removeHandler: routeOperations.deleteLocation,
  updateViewport: clientOperations.updateViewport,
};

export type RouteProps = DispatchProps & StateProps;

export interface RouteState {
  selectedId: number;
  selectedRouteId: number;
}

export class Route extends React.Component<RouteProps, RouteState> {
  constructor(props: RouteProps) {
    super(props);

    this.state = {
      selectedId: NaN,
      selectedRouteId: 0,
    };
    this.onSelectLocation = this.onSelectLocation.bind(this);
    this.handleMapClick = this.handleMapClick.bind(this);
  }

  handleMapClick(e: LeafletMouseEvent) {
    this.props.addHandler({ ...e.latlng, description: '' });
  }

  onSelectLocation(id: number) {
    this.setState({ selectedId: id });
  }

  render() {
    const { selectedId, selectedRouteId } = this.state;
    const {
      boundingBox,
      userViewport,
      depot,
      visits,
      routes,
      removeHandler,
      updateViewport,
    } = this.props;

    // FIXME quick hack to preserve route color by keeping its index
    const filteredRoutes = (
      routes.map((value, index) => (index === selectedRouteId ? value : { visits: [], track: [] }))
    );
    const filteredVisits: Location[] = routes.length > 0 ? routes[selectedRouteId].visits : [];
    return (
      <>
        <Title headingLevel="h1">Route</Title>
        <Split hasGutter>
          <SplitItem
            isFilled={false}
            style={sideBarStyle}
          >
            <Form>
              <FormSelect
                style={{ backgroundColor: 'white', marginBottom: 10 }}
                isDisabled={routes.length === 0}
                value={selectedRouteId}
                onChange={(e) => {
                  this.setState({ selectedRouteId: parseInt(e as unknown as string, 10) });
                }}
                aria-label="FormSelect Input"
              >
                {routes.map(
                  (route, index) => (
                    <FormSelectOption
                      isDisabled={false}
                      // eslint-disable-next-line react/no-array-index-key
                      key={index}
                      value={index}
                      label={route.vehicle.name}
                    />
                  ),
                )}
              </FormSelect>
            </Form>
            <LocationList
              depot={depot}
              visits={filteredVisits}
              removeHandler={removeHandler}
              selectHandler={this.onSelectLocation}
            />
          </SplitItem>
          <SplitItem isFilled>
            <RouteMap
              boundingBox={boundingBox}
              userViewport={userViewport}
              updateViewport={updateViewport}
              selectedId={selectedId}
              clickHandler={this.handleMapClick}
              removeHandler={removeHandler}
              depot={depot}
              visits={visits}
              routes={filteredRoutes}
            />
          </SplitItem>
        </Split>
      </>
    );
  }
}

export default connect(
  mapStateToProps,
  mapDispatchToProps,
)(Route);
