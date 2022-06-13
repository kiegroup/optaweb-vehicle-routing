import { ActionFactory } from '../types';
import {
  ActionType,
  AddLocationAction,
  AddVehicleAction,
  ClearRouteAction,
  DeleteLocationAction,
  DeleteVehicleAction,
  LatLngWithDescription,
  RoutingPlan,
  UpdateRouteAction,
} from './types';

export const addVehicle: ActionFactory<void, AddVehicleAction> = () => ({
  type: ActionType.ADD_VEHICLE,
});

export const deleteVehicle: ActionFactory<number, DeleteVehicleAction> = (id) => ({
  type: ActionType.DELETE_VEHICLE,
  value: id,
});

export const addLocation: ActionFactory<LatLngWithDescription, AddLocationAction> = (location) => ({
  type: ActionType.ADD_LOCATION,
  value: location,
});

export const deleteLocation: ActionFactory<number, DeleteLocationAction> = (id) => ({
  type: ActionType.DELETE_LOCATION,
  value: id,
});

export const clearRoute: ActionFactory<void, ClearRouteAction> = () => ({
  type: ActionType.CLEAR_SOLUTION,
});

export const updateRoute: ActionFactory<RoutingPlan, UpdateRouteAction> = (plan) => ({
  plan,
  type: ActionType.UPDATE_ROUTING_PLAN,
});
