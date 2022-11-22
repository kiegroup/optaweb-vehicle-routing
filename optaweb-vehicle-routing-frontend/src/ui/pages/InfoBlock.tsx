import { Flex, FlexItem, Tooltip } from '@patternfly/react-core';
import {
  ClockIcon as DistanceIcon,
  IconSize,
  MapMarkerIcon as VisitIcon,
  TruckIcon,
  WeightHangingIcon as CapacityIcon,
} from '@patternfly/react-icons';
import { SVGIconProps } from '@patternfly/react-icons/dist/js/createIcon';
import * as React from 'react';

interface InfoBlockProps {
  icon: React.ComponentClass<SVGIconProps>;
  content?: {
    data: string | number;
    minWidth: string;
  };
  color?: string;
  tooltip: string;
}

export const InfoBlock = ({ icon, content, tooltip, color }: InfoBlockProps) => {
  const Icon = icon;
  return (
    <Tooltip content={tooltip} position="bottom">
      <Flex spaceItems={{ default: 'spaceItemsSm' }}>
        <FlexItem>
          <Icon size={IconSize.md} color={color} />
        </FlexItem>
        {content && (
          <FlexItem style={{ minWidth: content.minWidth, textAlign: 'right' }}>
            {content.data}
          </FlexItem>
        )}
      </Flex>
    </Tooltip>
  );
};

interface CapacityInfoProps {
  totalDemand: number;
  totalCapacity: number;
}

export const CapacityInfo = ({
  totalDemand,
  totalCapacity,
}: CapacityInfoProps) => (
  <InfoBlock
    icon={CapacityIcon}
    content={{ data: `${totalDemand}/${totalCapacity}`, minWidth: '4em' }}
    color={totalDemand > totalCapacity ? 'var(--pf-global--danger-color--200)' : ''}
    tooltip="Capacity usage: total demand / total capacity"
  />
);

interface DistanceInfoProps {
  distance: string;
}

export const DistanceInfo = ({ distance }: DistanceInfoProps) => (
  <InfoBlock
    icon={DistanceIcon}
    content={{ data: distance, minWidth: '6.8em' }}
    tooltip="Total driving travel time spent by all vehicles"
  />
);

export const VehiclesInfo = () => (
  <InfoBlock icon={TruckIcon} tooltip="Vehicles" />
);

interface VisitInfoProps {
  visitCount: number;
}

export const VisitsInfo = ({ visitCount }: VisitInfoProps) => (
  <InfoBlock
    icon={VisitIcon}
    content={{ data: visitCount, minWidth: '2em' }}
    tooltip="Number of visits"
  />
);
