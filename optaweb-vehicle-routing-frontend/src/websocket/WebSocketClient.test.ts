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

import { sources } from 'eventsourcemock';
import { LatLngWithDescription } from 'store/route/types';
import WebSocketClient from './WebSocketClient';

describe('WebSocketClient', () => {
  const url = 'http://test.url:123/my-endpoint';
  const client = new WebSocketClient(url);

  const onSuccess = jest.fn();
  const onError = jest.fn();

  client.connect(onSuccess, onError);
  const source = sources[`${url}/events/route`];
  source.emitOpen();

  it('connect() should connect with success and error callbacks', () => {
    expect(source.onopen).toEqual(onSuccess);
    expect(source.onerror).toEqual(onError);
  });

  it('addLocation() should send location', () => {
    const location: LatLngWithDescription = {
      lat: 1,
      lng: 2,
      description: 'test',
    };

    client.addLocation(location);

    // expect(mockClient.send).toHaveBeenCalledWith('/app/location', JSON.stringify(location));
  });

  it('deleteLocation() should send location ID', () => {
    const locationId = 21;

    client.deleteLocation(locationId);

    // expect(mockClient.send).toHaveBeenCalledWith(`/app/location/${locationId}/delete`, JSON.stringify(locationId));
  });

  it('addVehicle() should add vehicle', () => {
    client.addVehicle();

    // expect(mockClient.send).toHaveBeenCalledWith('/app/vehicle');
  });

  it('deleteVehicle() should send vehicle ID', () => {
    const vehicleId = 34;

    client.deleteVehicle(vehicleId);

    // expect(mockClient.send).toHaveBeenCalledWith(`/app/vehicle/${vehicleId}/delete`, JSON.stringify(vehicleId));
  });

  it('deleteAnyVehicle() should send message to the correct destination', () => {
    client.deleteAnyVehicle();

    // expect(mockClient.send).toHaveBeenCalledWith('/app/vehicle/deleteAny');
  });

  it('deleteAnyVehicle() should send message to the correct destination', () => {
    const vehicleId = 7;
    const capacity = 54;

    client.changeVehicleCapacity(vehicleId, capacity);

    // expect(mockClient.send).toHaveBeenCalledWith(`/app/vehicle/${vehicleId}/capacity`, JSON.stringify(capacity));
  });

  it('loadDemo() should send demo name', () => {
    const demo = 'Test demo';

    client.loadDemo(demo);

    // expect(mockClient.send).toHaveBeenCalledWith(`/app/demo/${demo}`);
  });

  it('clear() should call clear endpoint', () => {
    client.clear();

    // expect(mockClient.send).toHaveBeenCalledWith('/app/clear');
  });

  it('subscribeToServerInfo() should subscribe with callback', () => {
    const callback = jest.fn();
    const payload = { value: 'test' };

    client.subscribeToServerInfo(callback);

    // expect(mockClient.subscribe.mock.calls[0][0]).toBe('/topic/serverInfo');
    // expect(typeof mockClient.subscribe.mock.calls[0][1]).toBe('function');

    // mockClient.subscribe.mock.calls[0][1]({ body: JSON.stringify(payload) });
    expect(callback).toHaveBeenCalledWith(payload);
  });

  it('subscribeToRoute() should subscribe with callback', () => {
    const callback = jest.fn();
    const payload = { msg: 'test' };
    const messageEvent = new MessageEvent('route', {
      data: JSON.stringify(payload),
    });

    client.subscribeToRoute(callback);

    source.emit(messageEvent.type, messageEvent);

    expect(callback).toHaveBeenCalledWith(payload);
  });

  it('subscribeToErrorTopic() should subscribe with callback', () => {
    const callback = jest.fn();
    const payload = { msg: 'test' };
    const messageEvent = new MessageEvent('errorMessage', {
      data: JSON.stringify(payload),
    });

    client.subscribeToErrorTopic(callback);

    source.emit(messageEvent.type, messageEvent);

    expect(callback).toHaveBeenCalledWith(payload);
  });
});
