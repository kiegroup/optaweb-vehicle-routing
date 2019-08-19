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

import * as SockJS from 'sockjs-client';
import { LatLngWithDescription } from 'store/route/types';
import { Client, Options, over } from 'webstomp-client';
import WebSocketClient from './WebSocketClient';

jest.mock('sockjs-client');
jest.mock('webstomp-client');

beforeEach(() => {
  jest.resetAllMocks();

  // @ts-ignore
  (over as unknown as jest.MockInstance<Client, [string, Options?]>).mockReturnValue(mockClient);
});

const mockClient = {
  connected: false,
  isBinary: false,
  partialData: '',
  subscriptions: null,
  ws: null,
  connect: jest.fn(),
  disconnect: jest.fn(),
  send: jest.fn(),
  subscribe: jest.fn(),
  unsubscribe: jest.fn(),
  begin: jest.fn(),
  commit: jest.fn(),
  abort: jest.fn(),
  ack: jest.fn(),
  nack: jest.fn(),
  debug: jest.fn(),
};

describe('WebSocketClient', () => {
  const url = 'http://test.url:123/my-endpoint';
  const client = new WebSocketClient(url);

  const onSuccess = jest.fn();
  const onError = jest.fn();

  it('connect() should connect with URL, success and error callbacks', () => {
    client.connect(onSuccess, onError);

    expect(SockJS).toHaveBeenCalledWith(url);
    expect(over).toHaveBeenCalled();

    expect(mockClient.connect).toHaveBeenCalledTimes(1);
    expect(mockClient.connect.mock.calls[0][0]).toEqual({});
    expect(mockClient.connect.mock.calls[0][1]).toEqual(onSuccess);
    expect(mockClient.connect.mock.calls[0][2]).toEqual(onError);
  });

  it('addLocation() should send location', () => {
    const location: LatLngWithDescription = {
      lat: 1,
      lng: 2,
      description: 'test',
    };

    client.connect(onSuccess, onError);
    client.addLocation(location);

    expect(mockClient.send).toHaveBeenCalledWith('/app/location', JSON.stringify(location));
  });

  it('deleteLocation() should send location ID', () => {
    const locationId = 21;

    client.connect(onSuccess, onError);
    client.deleteLocation(locationId);

    expect(mockClient.send).toHaveBeenCalledWith(`/app/location/${locationId}/delete`, JSON.stringify(locationId));
  });

  it('addVehicle() should add vehicle', () => {
    client.connect(onSuccess, onError);
    client.addVehicle();

    expect(mockClient.send).toHaveBeenCalledWith('/app/vehicle');
  });

  it('deleteVehicle() should send vehicle ID', () => {
    const vehicleId = 34;

    client.connect(onSuccess, onError);
    client.deleteVehicle(vehicleId);

    expect(mockClient.send).toHaveBeenCalledWith(`/app/vehicle/${vehicleId}/delete`, JSON.stringify(vehicleId));
  });

  it('deleteAnyVehicle() should send message to the correct destination', () => {
    client.connect(onSuccess, onError);
    client.deleteAnyVehicle();

    expect(mockClient.send).toHaveBeenCalledWith('/app/vehicle/deleteAny');
  });

  it('deleteAnyVehicle() should send message to the correct destination', () => {
    const vehicleId = 7;
    const capacity = 54;

    client.connect(onSuccess, onError);
    client.changeVehicleCapacity(vehicleId, capacity);

    expect(mockClient.send).toHaveBeenCalledWith(`/app/vehicle/${vehicleId}/capacity`, JSON.stringify(capacity));
  });

  it('loadDemo() should send demo name', () => {
    const demo = 'Test demo';

    client.connect(onSuccess, onError);
    client.loadDemo(demo);

    expect(mockClient.send).toHaveBeenCalledWith(`/app/demo/${demo}`);
  });

  it('clear() should call clear endpoint', () => {
    client.connect(onSuccess, onError);
    client.clear();

    expect(mockClient.send).toHaveBeenCalledWith('/app/clear');
  });

  it('subscribeToServerInfo() should subscribe with callback', () => {
    const callback = jest.fn();
    const payload = { value: 'test' };

    client.connect(onSuccess, onError);
    client.subscribeToServerInfo(callback);

    expect(mockClient.subscribe.mock.calls[0][0]).toBe('/topic/serverInfo');
    expect(typeof mockClient.subscribe.mock.calls[0][1]).toBe('function');

    mockClient.subscribe.mock.calls[0][1]({ body: JSON.stringify(payload) });
    expect(callback).toHaveBeenCalledWith(payload);
  });

  it('subscribeToRoute() should subscribe with callback', () => {
    const callback = jest.fn();
    const payload = { value: 'test' };

    client.connect(onSuccess, onError);
    client.subscribeToRoute(callback);

    expect(mockClient.subscribe.mock.calls[0][0]).toBe('/topic/route');
    expect(typeof mockClient.subscribe.mock.calls[0][1]).toBe('function');

    mockClient.subscribe.mock.calls[0][1]({ body: JSON.stringify(payload) });
    expect(callback).toHaveBeenCalledWith(payload);
  });
});
