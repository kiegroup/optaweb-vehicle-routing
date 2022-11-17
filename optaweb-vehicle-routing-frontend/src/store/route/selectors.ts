import { RoutingPlan } from 'store/route/types';

export const totalCapacity = (plan: RoutingPlan): number => plan.vehicles
  .map((vehicle) => vehicle.capacity)
  .reduce((a, b) => a + b, 0);

export const totalDemand = (plan: RoutingPlan): number => plan.visits.length;
