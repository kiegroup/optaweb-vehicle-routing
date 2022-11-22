import {
  Button,
  ButtonVariant,
  Flex,
  FlexItem,
  InputGroup,
  InputGroupText,
  Split,
  SplitItem,
  Title,
} from '@patternfly/react-core';
import { MinusIcon, PlusIcon } from '@patternfly/react-icons';
import { backendUrl } from 'common';
import { LeafletMouseEvent } from 'leaflet';
import * as React from 'react';
import { connect } from 'react-redux';
import { clientOperations } from 'store/client';
import { UserViewport } from 'store/client/types';
import { demoOperations } from 'store/demo';
import { routeOperations, routeSelectors } from 'store/route';
import { Location, RouteWithTrack } from 'store/route/types';
import { BoundingBox } from 'store/server/types';
import { AppState } from 'store/types';
import { DemoDropdown } from 'ui/components/DemoDropdown';
import LocationList from 'ui/components/LocationList';
import RouteMap from 'ui/components/RouteMap';
import SearchBox, { Result } from 'ui/components/SearchBox';
import { sideBarStyle } from 'ui/pages/common';
import { CapacityInfo, DistanceInfo, VehiclesInfo, VisitsInfo } from 'ui/pages/InfoBlock';

export interface StateProps {
  distance: string;
  depot: Location | null;
  vehicleCount: number;
  totalCapacity: number;
  totalDemand: number;
  visits: Location[];
  routes: RouteWithTrack[];
  isDemoLoading: boolean;
  boundingBox: BoundingBox | null;
  userViewport: UserViewport;
  countryCodeSearchFilter: string[];
  demoNames: string[];
}

export interface DispatchProps {
  loadHandler: typeof demoOperations.requestDemo;
  clearHandler: typeof routeOperations.clearRoute;
  addLocationHandler: typeof routeOperations.addLocation;
  removeLocationHandler: typeof routeOperations.deleteLocation;
  addVehicleHandler: typeof routeOperations.addVehicle;
  removeVehicleHandler: typeof routeOperations.deleteAnyVehicle;
  updateViewport: typeof clientOperations.updateViewport;
}

const mapStateToProps = ({ plan, demo, serverInfo, userViewport }: AppState): StateProps => ({
  distance: plan.distance,
  vehicleCount: plan.vehicles.length,
  totalCapacity: routeSelectors.totalCapacity(plan),
  totalDemand: routeSelectors.totalDemand(plan),
  depot: plan.depot,
  visits: plan.visits,
  routes: plan.routes,
  isDemoLoading: demo.isLoading,
  boundingBox: serverInfo.boundingBox,
  countryCodeSearchFilter: serverInfo.countryCodes,
  // TODO use selector
  // TODO sort demos alphabetically?
  demoNames: (serverInfo.demos && serverInfo.demos.map((value) => value.name)) || [],
  userViewport,
});

const mapDispatchToProps: DispatchProps = {
  loadHandler: demoOperations.requestDemo,
  clearHandler: routeOperations.clearRoute,
  addLocationHandler: routeOperations.addLocation,
  removeLocationHandler: routeOperations.deleteLocation,
  addVehicleHandler: routeOperations.addVehicle,
  removeVehicleHandler: routeOperations.deleteAnyVehicle,
  updateViewport: clientOperations.updateViewport,
};

export type DemoProps = DispatchProps & StateProps;

export interface DemoState {
  selectedId: number;
}

export class Demo extends React.Component<DemoProps, DemoState> {
  constructor(props: DemoProps) {
    super(props);

    this.state = {
      selectedId: NaN,
    };
    this.handleDemoLoadClick = this.handleDemoLoadClick.bind(this);
    this.handleMapClick = this.handleMapClick.bind(this);
    this.handleSearchResultClick = this.handleSearchResultClick.bind(this);
    this.onSelectLocation = this.onSelectLocation.bind(this);
  }

  handleMapClick(e: LeafletMouseEvent) {
    this.props.addLocationHandler({ ...e.latlng, description: '' }); // TODO use reverse geocoding to find address
  }

  handleSearchResultClick(result: Result) {
    this.props.addLocationHandler({ ...result.latLng, description: result.address });
  }

  handleDemoLoadClick(demoName: string) {
    this.props.loadHandler(demoName);
  }

  onSelectLocation(id: number) {
    this.setState({ selectedId: id });
  }

  render() {
    const { selectedId } = this.state;
    const {
      distance,
      depot,
      vehicleCount,
      totalCapacity,
      totalDemand,
      visits,
      routes,
      demoNames,
      isDemoLoading,
      boundingBox,
      userViewport,
      countryCodeSearchFilter,
      addVehicleHandler,
      removeVehicleHandler,
      removeLocationHandler,
      clearHandler,
      updateViewport,
    } = this.props;

    const exportDataSet = () => {
      window.open(`${backendUrl}/api/dataset/export`);
    };

    return (
      // FIXME find a way to avoid these style customizations
      <Split hasGutter style={{ overflowY: 'auto' }}>
        <SplitItem
          isFilled={false}
          style={sideBarStyle}
        >
          <Title headingLevel="h1">Demo</Title>
          <SearchBox
            boundingBox={boundingBox}
            countryCodeSearchFilter={countryCodeSearchFilter}
            addHandler={this.handleSearchResultClick}
          />
          <LocationList
            depot={depot}
            visits={visits}
            removeHandler={removeLocationHandler}
            selectHandler={this.onSelectLocation}
          />
        </SplitItem>

        <SplitItem
          isFilled
          style={{ display: 'flex', flexDirection: 'column' }}
        >
          <Flex justifyContent={{ default: 'justifyContentSpaceBetween' }}>
            <FlexItem>
              <VisitsInfo visitCount={visits.length} />
            </FlexItem>
            <FlexItem>
              <CapacityInfo totalDemand={totalDemand} totalCapacity={totalCapacity} />
            </FlexItem>
            <FlexItem>
              <Flex>
                <FlexItem>
                  <VehiclesInfo />
                </FlexItem>
                <FlexItem>
                  <InputGroup>
                    <Button
                      variant={ButtonVariant.primary}
                      isDisabled={vehicleCount === 0}
                      onClick={removeVehicleHandler}
                    >
                      <MinusIcon />
                    </Button>
                    <InputGroupText readOnly style={{ minWidth: '2.5em' }}>
                      {vehicleCount}
                    </InputGroupText>
                    <Button
                      variant={ButtonVariant.primary}
                      onClick={addVehicleHandler}
                      data-cy="demo-add-vehicle"
                    >
                      <PlusIcon />
                    </Button>
                  </InputGroup>
                </FlexItem>
              </Flex>
            </FlexItem>
            <FlexItem>
              <DistanceInfo distance={distance} />
            </FlexItem>
            <FlexItem>
              <Button
                isDisabled={!depot || isDemoLoading}
                style={{ marginBottom: 16, marginLeft: 16 }}
                onClick={exportDataSet}
              >
                Export
              </Button>
              {(depot === null && (
                <DemoDropdown
                  demos={demoNames}
                  onSelect={this.handleDemoLoadClick}
                />
              )) || (
                <Button
                  isDisabled={isDemoLoading}
                  style={{ marginBottom: 16, marginLeft: 16 }}
                  onClick={clearHandler}
                  data-cy="demo-clear-button"
                >
                  Clear
                </Button>
              )}
            </FlexItem>
          </Flex>
          <RouteMap
            boundingBox={boundingBox}
            userViewport={userViewport}
            updateViewport={updateViewport}
            selectedId={selectedId}
            clickHandler={this.handleMapClick}
            removeHandler={removeLocationHandler}
            depot={depot}
            routes={routes}
            visits={visits}
          />
        </SplitItem>
      </Split>
    );
  }
}

export default connect(
  mapStateToProps,
  mapDispatchToProps,
)(Demo);
