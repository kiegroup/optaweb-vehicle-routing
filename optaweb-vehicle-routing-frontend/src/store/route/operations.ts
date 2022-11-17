import { AppState, Dispatch, ThunkCommandFactory } from '../types';
import * as actions from './actions';
import {
  AddLocationAction,
  AddVehicleAction,
  ClearRouteAction,
  DeleteLocationAction,
  DeleteVehicleAction,
  LatLngWithDescription,
  VehicleCapacity,
} from './types';
import WebSocketClient from '../../websocket/WebSocketClient';

export const { updateRoute } = actions;

export const addLocation: ThunkCommandFactory<LatLngWithDescription, AddLocationAction> = (
  (location) => (dispatch: Dispatch<AddLocationAction>, _getState: () => AppState, client: WebSocketClient): void => {
    dispatch(actions.addLocation(location));
    client.addLocation(location);
  });

export const deleteLocation: ThunkCommandFactory<number, DeleteLocationAction> = (
  (locationId) => (
    (dispatch: Dispatch<DeleteLocationAction>, _getState: () => AppState, client: WebSocketClient): void => {
      dispatch(actions.deleteLocation(locationId));
      client.deleteLocation(locationId);
    }));

export const addVehicle: ThunkCommandFactory<void, AddVehicleAction> = (
  () => (dispatch: Dispatch<AddVehicleAction>, _getState: () => AppState, client: WebSocketClient): void => {
    dispatch(actions.addVehicle());
    client.addVehicle();
  });

export const deleteVehicle: ThunkCommandFactory<number, DeleteVehicleAction> = (
  (vehicleId) => (
    (dispatch: Dispatch<DeleteVehicleAction>, _getState: () => AppState, client: WebSocketClient): void => {
      dispatch(actions.deleteVehicle(vehicleId));
      client.deleteVehicle(vehicleId);
    }));

export const deleteAnyVehicle: ThunkCommandFactory<void, never> = (
  () => (_dispatch: Dispatch<never>, _getState: () => AppState, client: WebSocketClient): void => {
    client.deleteAnyVehicle();
  });

export const changeVehicleCapacity: ThunkCommandFactory<VehicleCapacity, never> = (
  ({ vehicleId, capacity }: VehicleCapacity) => (
    (_dispatch: Dispatch<never>, _getState: () => AppState, client: WebSocketClient): void => {
      client.changeVehicleCapacity(vehicleId, capacity);
    }));

export const clearRoute: ThunkCommandFactory<void, ClearRouteAction> = (
  () => (dispatch: Dispatch<ClearRouteAction>, _getState: () => AppState, client: WebSocketClient): void => {
    dispatch(actions.clearRoute());
    client.clear();
  });
