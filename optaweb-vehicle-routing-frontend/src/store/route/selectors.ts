import { RoutingPlan } from 'store/route/types';

export const totalCapacity = (plan: RoutingPlan) => plan.vehicles
  .map((vehicle) => vehicle.capacity)
  .reduce((a, b) => a + b, 0);

export const totalDemand = (plan: RoutingPlan) => plan.visits.length;
