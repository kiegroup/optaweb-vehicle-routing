import { MessagePayload } from 'store/message/types';
import { LatLngWithDescription, RoutingPlan } from 'store/route/types';
import { ServerInfo } from 'store/server/types';

export default class WebSocketClient {
  readonly backendUrl: string;

  eventSource: EventSource | null;

  constructor(backendUrl: string) {
    this.backendUrl = backendUrl;
    this.eventSource = null;
  }

  connect(successCallback: () => void, errorCallback: (err: Event) => void): void {
    if (this.eventSource === null) {
      this.eventSource = new EventSource(`${this.backendUrl}/events`);
      this.eventSource.onopen = successCallback;
      this.eventSource.onerror = (event) => {
        // Each time a connection error happens...
        if (this.eventSource) {
          // ...close the eventSource...
          this.eventSource.close();
          this.eventSource = null;
        }
        // ...and invoke the errorCallback, which dispatches a single connectClient thunk action.
        // That forms an infinite loop, so the connection will be re-attempted until it succeeds.
        errorCallback((event));
      };
    }
  }

  addLocation(latLng: LatLngWithDescription): Promise<Response> {
    return fetch(`${this.backendUrl}/location`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(latLng),
    });
  }

  addVehicle(): Promise<Response> {
    return fetch(`${this.backendUrl}/vehicle`, { method: 'POST' });
  }

  loadDemo(name: string): Promise<Response> {
    return fetch(`${this.backendUrl}/demo/${name}`, { method: 'POST' });
  }

  deleteLocation(locationId: number): Promise<Response> {
    // TODO error callback
    return fetch(`${this.backendUrl}/location/${locationId}`, { method: 'DELETE' });
  }

  deleteAnyVehicle(): Promise<Response> {
    return fetch(`${this.backendUrl}/vehicle/deleteAny`, { method: 'POST' });
  }

  deleteVehicle(vehicleId: number): Promise<Response> {
    return fetch(`${this.backendUrl}/vehicle/${vehicleId}`, { method: 'DELETE' });
  }

  changeVehicleCapacity(vehicleId: number, capacity: number): Promise<Response> {
    return fetch(`${this.backendUrl}/vehicle/${vehicleId}/capacity`, {
      method: 'POST',
      body: JSON.stringify(capacity),
    });
  }

  clear(): Promise<Response> {
    return fetch(`${this.backendUrl}/clear`, { method: 'POST' });
  }

  subscribeToServerInfo(subscriptionCallback: (serverInfo: ServerInfo) => void): Promise<void> {
    return fetch(`${this.backendUrl}/serverInfo`)
      .then((response) => response.json())
      .then((data) => subscriptionCallback(data));
  }

  subscribeToRoute(subscriptionCallback: (plan: RoutingPlan) => void): void {
    if (this.eventSource !== null) {
      this.eventSource.addEventListener('route', (event: MessageEvent) => {
        subscriptionCallback(JSON.parse(event.data));
      });
    }
  }

  subscribeToErrorTopic(subscriptionCallback: (errorMessage: MessagePayload) => void): void {
    if (this.eventSource !== null) {
      this.eventSource.addEventListener('errorMessage', (event: MessageEvent) => {
        subscriptionCallback(JSON.parse(event.data));
      });
    }
  }
}
