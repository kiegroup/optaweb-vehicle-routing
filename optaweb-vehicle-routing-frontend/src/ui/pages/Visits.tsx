import { Title } from '@patternfly/react-core';
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
  removeHandler: typeof routeOperations.deleteLocation;
}

const mapDispatchToProps: DispatchProps = {
  removeHandler: routeOperations.deleteLocation,
};

export type Props = StateProps & DispatchProps;

export const Visits: React.FC<Props> = ({
  depot,
  visits,
  removeHandler,
}: Props) => (
  <>
    <Title headingLevel="h1">{`Visits (${visits.length})`}</Title>
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
