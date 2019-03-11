/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import { ILocation, IRoutingPlan } from './types';

export const getDomicileId = (plan: IRoutingPlan) => {
  if (plan.routes.length === 0) {
    return -1;
  }
  return plan.routes[0].visits.length > 0 ? plan.routes[0].visits[0].id : -1;
};

function reducer<T>(accumulator: T[], currentValue: T[]): T[] {
  return accumulator.concat(currentValue);
}

export const getVisits = (plan: IRoutingPlan): ILocation[] => {
  if (plan.routes.length === 0) {
    return [];
  }

  return plan.routes.map(route => route.visits).reduce(reducer, []);
};
