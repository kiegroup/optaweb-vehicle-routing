/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import { Flex, FlexItem, FlexModifiers, Tooltip } from '@patternfly/react-core';
import {
  ClockIcon as DistanceIcon,
  IconSize,
  MapMarkerIcon as VisitIcon,
  TruckIcon,
  WeightHangingIcon as CapacityIcon,
} from '@patternfly/react-icons';
import { IconType } from '@patternfly/react-icons/dist/js/createIcon';
import * as React from 'react';

interface InfoBlockProps {
  icon: IconType;
  data?: string | number;
  color?: string;
  tooltip: string;
}

export const InfoBlock = ({ icon, data, tooltip, color }: InfoBlockProps) => {
  const Icon = icon;
  return (
    <Tooltip content={tooltip} position="bottom">
      <Flex breakpointMods={[{ modifier: FlexModifiers['space-items-sm'] }]}>
        <FlexItem>
          <Icon size={IconSize.md} color={color} />
        </FlexItem>
        {data && (
          <FlexItem>
            {data}
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
    data={`${totalDemand}/${totalCapacity}`}
    color={totalDemand > totalCapacity ? 'var(--pf-global--danger-color--200)' : ''}
    tooltip="Capacity usage: total demand / total capacity"
  />
);

interface DistanceInfoProps {
  distance: string;
}

export const DistanceInfo = ({ distance }: DistanceInfoProps) => (
  <InfoBlock icon={DistanceIcon} data={distance} tooltip="Total driving travel time spent by all vehicles" />
);

export const VehiclesInfo = () => (
  <InfoBlock icon={TruckIcon} tooltip="Vehicles" />
);

interface VisitInfoProps {
  visitCount: number;
}

export const VisitsInfo = ({ visitCount }: VisitInfoProps) => (
  <InfoBlock icon={VisitIcon} data={visitCount} tooltip="Number of visits" />
);
