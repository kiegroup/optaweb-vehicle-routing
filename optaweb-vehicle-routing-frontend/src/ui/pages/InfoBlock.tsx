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
import { ClockIcon, IconSize, MessagesIcon, TruckIcon } from '@patternfly/react-icons';
import { IconType } from '@patternfly/react-icons/dist/js/createIcon';
import * as React from 'react';

interface InfoBlockProps {
  icon: IconType;
  data?: string | number;
  tooltip: string;
}

export const InfoBlock = ({ icon, data, tooltip }: InfoBlockProps) => {
  const Icon = icon;
  return (
    <Tooltip content={tooltip} position="bottom">
      <Flex breakpointMods={[{ modifier: FlexModifiers['space-items-sm'] }]}>
        <FlexItem>
          <Icon size={IconSize.md} />
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

export const VehiclesInfo = () => (
  <InfoBlock icon={TruckIcon} tooltip="Vehicles" />
);

interface VisitInfoProps {
  visitCount: number;
}

export const VisitsInfo = ({ visitCount }: VisitInfoProps) => (
  <InfoBlock icon={MessagesIcon} data={visitCount} tooltip="Number of visits" />
);

interface DistanceInfoProps {
  distance: string;
}

export const DistanceInfo = ({ distance }: DistanceInfoProps) => (
  <InfoBlock icon={ClockIcon} data={distance} tooltip="Total travel time" />
);
