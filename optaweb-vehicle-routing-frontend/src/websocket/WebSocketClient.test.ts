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
  const onSuccess = jest.fn();
  const onError = jest.fn();

  const connectClient = () => {
    const client = new WebSocketClient(url);
    client.connect(onSuccess, onError);
    const source = sources[`${url}/events`];
    source.emitOpen();
    return { client, source };
  };

  it('Error callback should be called on EventSource error event', () => {
    const { source } = connectClient();
    source.onerror();
    expect(onError).toBeCalled();
    expect(source);
  });

  it('Success callback should be called on EventSource open event', () => {
    const { source } = connectClient();
    source.onopen();
    expect(onSuccess).toBeCalled();
  });

  it('addLocation() should send location', () => {
    const location: LatLngWithDescription = {
      lat: 1,
      lng: 2,
      description: 'test',
    };
    fetchMock.postOnce('*', 200);
    const { client } = connectClient();

    client.addLocation(location);

    expect(fetchMock).toHaveLastFetched(`${url}/location`, { body: location });
  });

  it('deleteLocation() should send location ID', () => {
    const locationId = 21;
    fetchMock.deleteOnce('*', 200);
    const { client } = connectClient();

    client.deleteLocation(locationId);

    expect(fetchMock).toHaveLastFetched(`${url}/location/${locationId}`);
  });

  it('addVehicle() should add vehicle', () => {
    fetchMock.postOnce('*', 200);
    const { client } = connectClient();

    client.addVehicle();

    expect(fetchMock).toHaveLastFetched(`${url}/vehicle`);
  });

  it('deleteVehicle() should send vehicle ID', () => {
    const vehicleId = 34;
    fetchMock.deleteOnce('*', 200);
    const { client } = connectClient();

    client.deleteVehicle(vehicleId);

    expect(fetchMock).toHaveLastFetched(`${url}/vehicle/${vehicleId}`);
  });

  it('deleteAnyVehicle() should send message to the correct destination', () => {
    fetchMock.postOnce('*', 200);
    const { client } = connectClient();

    client.deleteAnyVehicle();

    expect(fetchMock).toHaveLastFetched(`${url}/vehicle/deleteAny`);
  });

  it('changeVehicleCapacity() should change capacity', () => {
    const vehicleId = 7;
    const capacity = 54;
    fetchMock.postOnce('*', 200);
    const { client } = connectClient();

    client.changeVehicleCapacity(vehicleId, capacity);

    expect(fetchMock).toHaveLastFetched(`${url}/vehicle/${vehicleId}/capacity`, {
      body: capacity as unknown as undefined,
    });
  });

  it('loadDemo() should send demo name', () => {
    const demo = 'Test demo';
    fetchMock.postOnce('*', 200);
    const { client } = connectClient();

    client.loadDemo(demo);

    expect(fetchMock).toHaveLastFetched(`${url}/demo/${demo}`);
  });

  it('clear() should call clear endpoint', () => {
    fetchMock.postOnce('*', 200);
    const { client } = connectClient();
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
    const { client } = connectClient();

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
    const { client, source } = connectClient();

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
    const { client, source } = connectClient();

    client.subscribeToErrorTopic(callback);

    source.emit(messageEvent.type, messageEvent);

    expect(callback).toHaveBeenCalledWith(payload);
  });
});
