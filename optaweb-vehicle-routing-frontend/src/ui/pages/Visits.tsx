import { Text, TextContent, TextVariants } from '@patternfly/react-core';
import * as React from 'react';
import { connect } from 'react-redux';
import { routeOperations } from 'store/route';
import { Location } from 'store/route/types';
import { AppState } from 'store/types';
import LocationList from 'ui/components/LocationList';

interface StateProps {
  depot: Location | null;
  visits: Location[];
}

const mapStateToProps = ({ plan }: AppState): StateProps => ({
  depot: plan.depot,
  visits: plan.visits,
});

export interface DispatchProps {
  addHandler: typeof routeOperations.addLocation;
  removeHandler: typeof routeOperations.deleteLocation;
}

const mapDispatchToProps: DispatchProps = {
  addHandler: routeOperations.addLocation,
  removeHandler: routeOperations.deleteLocation,
};

export type Props = StateProps & DispatchProps;

export const Visits: React.FC<Props> = ({
  depot,
  visits,
  removeHandler,
}: Props) => (
  <>
    <TextContent>
      <Text component={TextVariants.h1}>{`Visits (${visits.length})`}</Text>
    </TextContent>
    {/* TODO do not show depots */}
    <LocationList
      removeHandler={removeHandler}
      selectHandler={() => undefined}
      depot={depot}
      visits={visits}
    />
  </>
);

export default connect(mapStateToProps, mapDispatchToProps)(Visits);
