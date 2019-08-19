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

import { mockStore } from '../mockStore';
import { AppState } from '../types';
import { WebSocketConnectionStatus } from '../websocket/types';
import * as actions from './actions';
import reducer, { routeOperations } from './index';
import { initialRouteState } from './reducers';
import { LatLngWithDescription, Vehicle, VehicleCapacity } from './types';

describe('Route operations', () => {
  it('clearRoute() should call client', () => {
    const { store, client } = mockStore(state);

    store.dispatch(routeOperations.clearRoute());

    expect(store.getActions()).toEqual([actions.clearRoute()]);
    expect(client.clear).toHaveBeenCalledTimes(1);
  });

  it('deleteLocation() should call client', () => {
    const { store, client } = mockStore(state);
    const id = 3214;

    store.dispatch(routeOperations.deleteLocation(id));

    expect(store.getActions()).toEqual([actions.deleteLocation(id)]);
    expect(client.deleteLocation).toHaveBeenCalledTimes(1);
    expect(client.deleteLocation).toHaveBeenCalledWith(id);
  });

  it('deleteVehicle() should call client', () => {
    const { store, client } = mockStore(state);
    const id = 5;

    store.dispatch(routeOperations.deleteVehicle(id));

    expect(store.getActions()).toEqual([actions.deleteVehicle(id)]);
    expect(client.deleteVehicle).toHaveBeenCalledTimes(1);
    expect(client.deleteVehicle).toHaveBeenCalledWith(id);
  });

  it('deleteAnyVehicle() should call client', () => {
    const { store, client } = mockStore(state);

    store.dispatch(routeOperations.deleteAnyVehicle());

    expect(store.getActions()).toEqual([]);
    expect(client.deleteAnyVehicle).toHaveBeenCalledTimes(1);
  });

  it('addLocation() should call client', () => {
    const { store, client } = mockStore(state);
    const location: LatLngWithDescription = { lat: 11.01, lng: -35.79, description: 'new location' };

    store.dispatch(routeOperations.addLocation(location));

    expect(store.getActions()).toEqual([actions.addLocation(location)]);
    expect(client.addLocation).toHaveBeenCalledTimes(1);
    expect(client.addLocation).toHaveBeenCalledWith(location);
  });

  it('addVehicle() should call client', () => {
    const { store, client } = mockStore(state);

    store.dispatch(routeOperations.addVehicle());

    expect(store.getActions()).toEqual([actions.addVehicle()]);
    expect(client.addVehicle).toHaveBeenCalledTimes(1);
    expect(client.addVehicle).toHaveBeenCalledWith();
  });

  it('changeVehicleCapacity() should call client', () => {
    const { store, client } = mockStore(state);

    const capacityChange: VehicleCapacity = { vehicleId: 5, capacity: 50 };
    store.dispatch(routeOperations.changeVehicleCapacity(capacityChange));

    expect(store.getActions()).toEqual([]);
    expect(client.changeVehicleCapacity).toHaveBeenCalledWith(capacityChange.vehicleId, capacityChange.capacity);
  });
});

describe('Route reducers', () => {
  it('clear route', () => {
    expect(
      reducer(state.plan, actions.clearRoute()),
    ).toEqual(state.plan);
  });

  it('add location', () => {
    expect(
      reducer(state.plan, actions.addLocation({
        lat: 1,
        lng: -1,
        description: 'description',
      })),
    ).toEqual(state.plan);
  });

  it('delete location', () => {
    expect(
      reducer(state.plan, actions.deleteLocation(1)),
    ).toEqual(state.plan);
  });

  it('add vehicle', () => {
    expect(
      reducer(state.plan, actions.addVehicle()),
    ).toEqual(state.plan);
  });

  it('delete vehicle', () => {
    expect(
      reducer(state.plan, actions.deleteVehicle(7)),
    ).toEqual(state.plan);
  });

  it('update route', () => {
    expect(
      reducer(initialRouteState, actions.updateRoute(state.plan)),
    ).toEqual(state.plan);
  });
});

const vehicle1: Vehicle = { id: 1, name: 'v1', capacity: 5 };
const vehicle2: Vehicle = { id: 2, name: 'v2', capacity: 5 };
const visit1 = {
  id: 1,
  lat: 1.345678,
  lng: 1.345678,
};
const visit2 = {
  id: 2,
  lat: 2.345678,
  lng: 2.345678,
};
const visit3 = {
  id: 3,
  lat: 3.676111,
  lng: 3.568333,
};
const visit4 = {
  id: 4,
  lat: 4.345678,
  lng: 4.345678,
};
const visit5 = {
  id: 5,
  lat: 5.345678,
  lng: 5.345678,
};

const state: AppState = {
  connectionStatus: WebSocketConnectionStatus.CLOSED,
  serverInfo: {
    boundingBox: null,
    countryCodes: [],
    demos: [],
  },
  userViewport: {
    isDirty: false,
    zoom: 1,
    center: [0, 0],
  },
  demo: {
    demoName: null,
    isLoading: false,
  },
  plan: {
    distance: '10',
    vehicles: [
      vehicle1,
      vehicle2,
    ],
    depot: null,
    visits: [visit1, visit2, visit3, visit4, visit5],
    routes: [{
      vehicle: vehicle1,
      visits: [visit1, visit2, visit3],
      track: [[0.111222, 0.222333], [0.444555, 0.555666]],
    }, {
      vehicle: vehicle2,
      visits: [visit4, visit5],

      track: [[0.41, 0.42], [0.51, 0.52]],
    }],
  },
};
