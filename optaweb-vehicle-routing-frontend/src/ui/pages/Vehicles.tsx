import {
  Button,
  DataList,
  GutterSize,
  Split,
  SplitItem,
  Text,
  TextContent,
  TextVariants,
} from '@patternfly/react-core';
import * as React from 'react';
import { connect } from 'react-redux';
import { routeOperations } from 'store/route';
import { Vehicle } from 'store/route/types';
import { AppState } from 'store/types';
import VehicleItem from 'ui/components/Vehicle';

interface StateProps {
  vehicles: Vehicle[];
}

interface DispatchProps {
  addVehicleHandler: typeof routeOperations.addVehicle;
  removeVehicleHandler: typeof routeOperations.deleteVehicle;
  changeVehicleCapacityHandler: typeof routeOperations.changeVehicleCapacity;
}

export type Props = StateProps & DispatchProps;

const mapStateToProps = ({ plan }: AppState): StateProps => ({
  vehicles: plan.vehicles,
});

const mapDispatchToProps: DispatchProps = {
  addVehicleHandler: routeOperations.addVehicle,
  removeVehicleHandler: routeOperations.deleteVehicle,
  changeVehicleCapacityHandler: routeOperations.changeVehicleCapacity,
};

export const Vehicles: React.FC<Props> = ({
  vehicles, addVehicleHandler, removeVehicleHandler, changeVehicleCapacityHandler,
}) => (
  <>
    <Split gutter={GutterSize.md} style={{ overflowY: 'auto' }}>
      <SplitItem isFilled>
        <TextContent>
          <Text component={TextVariants.h1}>{`Vehicles (${vehicles.length})`}</Text>
        </TextContent>
      </SplitItem>
      <SplitItem isFilled={false}>
        <Button
          style={{ marginBottom: 16, marginLeft: 16 }}
          onClick={addVehicleHandler}
        >
          Add
        </Button>
      </SplitItem>
    </Split>
    <div style={{ overflowY: 'auto' }}>
      <DataList
        aria-label="List of vehicles"
      >
        {vehicles
          .slice(0) // clone the array because
          // sort is done in place (that would affect the route)
          .sort((a, b) => a.id - b.id)
          .map((vehicle) => (
            <VehicleItem
              key={vehicle.id}
              id={vehicle.id}
              description={vehicle.name}
              capacity={vehicle.capacity}
              removeHandler={removeVehicleHandler}
              capacityChangeHandler={changeVehicleCapacityHandler}
            />
          ))}
      </DataList>
    </div>
  </>
);

export default connect(mapStateToProps, mapDispatchToProps)(Vehicles);
