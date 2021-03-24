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
import fetchMock from 'fetch-mock-jest';
import { LatLngWithDescription } from 'store/route/types';
import WebSocketClient from './WebSocketClient';

beforeEach(() => {
  // Learn about fetch-mock: http://www.wheresrhys.co.uk/fetch-mock/.
  fetchMock.reset();
});

describe('WebSocketClient', () => {
  const url = 'http://test.url:123/my-endpoint';
  const client = new WebSocketClient(url);

  const onSuccess = jest.fn();
  const onError = jest.fn();

  client.connect(onSuccess, onError);
  const source = sources[`${url}/events`];
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
    fetchMock.postOnce('*', 200);

    client.addLocation(location);

    expect(fetchMock).toHaveLastFetched(`${url}/location`, { body: location });
  });

  it('deleteLocation() should send location ID', () => {
    const locationId = 21;
    fetchMock.deleteOnce('*', 200);

    client.deleteLocation(locationId);

    expect(fetchMock).toHaveLastFetched(`${url}/location/${locationId}`);
  });

  it('addVehicle() should add vehicle', () => {
    fetchMock.postOnce('*', 200);

    client.addVehicle();

    expect(fetchMock).toHaveLastFetched(`${url}/vehicle`);
  });

  it('deleteVehicle() should send vehicle ID', () => {
    const vehicleId = 34;
    fetchMock.deleteOnce('*', 200);

    client.deleteVehicle(vehicleId);

    expect(fetchMock).toHaveLastFetched(`${url}/vehicle/${vehicleId}`);
  });

  it('deleteAnyVehicle() should send message to the correct destination', () => {
    fetchMock.postOnce('*', 200);

    client.deleteAnyVehicle();

    expect(fetchMock).toHaveLastFetched(`${url}/vehicle/deleteAny`);
  });

  it('changeVehicleCapacity() should change capacity', () => {
    const vehicleId = 7;
    const capacity = 54;
    fetchMock.postOnce('*', 200);

    client.changeVehicleCapacity(vehicleId, capacity);

    expect(fetchMock).toHaveLastFetched(`${url}/vehicle/${vehicleId}/capacity`, {
      body: capacity as unknown as object,
    });
  });

  it('loadDemo() should send demo name', () => {
    const demo = 'Test demo';
    fetchMock.postOnce('*', 200);

    client.loadDemo(demo);

    expect(fetchMock).toHaveLastFetched(`${url}/demo/${demo}`);
  });

  it('clear() should call clear endpoint', () => {
    fetchMock.postOnce('*', 200);
    client.clear();
    expect(fetchMock).toHaveLastFetched(`${url}/clear`);
  });

  it('subscribeToServerInfo() should subscribe with callback', async () => {
    const callback = jest.fn();
    const payload = { value: 'test' };
    fetchMock.getOnce(`${url}/serverInfo`, {
      status: 200,
      body: JSON.stringify(payload),
    });

    await client.subscribeToServerInfo(callback);

    expect(fetchMock).toBeDone();
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
