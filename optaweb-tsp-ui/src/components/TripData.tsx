import { Progress } from '@patternfly/react-core';
import * as React from 'react';

export interface ITripDataProps {
  distance: number;
  maxDistance: number;
}

export default function TripData({ distance, maxDistance }: ITripDataProps) {
  return (
    <Progress
      value={Math.floor((distance / maxDistance) * 100)}
      title={`Max: ${maxDistance}, Current: ${distance} km`}
      valueText={`${distance} km`}
      measureLocation={'none'}
    />
  );
}
