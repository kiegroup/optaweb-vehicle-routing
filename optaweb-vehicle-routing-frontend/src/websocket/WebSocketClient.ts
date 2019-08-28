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

import SockJS from 'sockjs-client';
import { LatLngWithDescription, RoutingPlan } from 'store/route/types';
import { ServerInfo } from 'store/server/types';
import { Client, Frame, over } from 'webstomp-client';

export default class WebSocketClient {
  readonly socketUrl: string;

  stompClient: Client | null;

  constructor(socketUrl: string) {
    this.socketUrl = socketUrl;
    this.stompClient = null;
  }

  connect(successCallback: () => any, errorCallback: (err: CloseEvent | Frame) => any) {
    const webSocket = new SockJS(this.socketUrl);
    this.stompClient = over(webSocket, {
      debug: true,
      // Because webstomp first reads ws.protocol:
      // https://github.com/JSteunou/webstomp-client/blob/1.2.6/src/client.js#L152
      // but SockJS doesn't specify it:
      // https://github.com/sockjs/sockjs-client/blob/v1.3.0/lib/main.js#L43
      // so finally this will be used to set accept-version header to '1.2' (verify in browser console):
      // (see also https://github.com/JSteunou/webstomp-client/issues/75)
      protocols: ['v12.stomp'],
    });
    this.stompClient.connect(
      {}, // no headers
      successCallback,
      errorCallback,
    );
  }

  addLocation(latLng: LatLngWithDescription) {
    if (this.stompClient) {
      this.stompClient.send('/app/location', JSON.stringify(latLng));
    }
  }

  addVehicle() {
    if (this.stompClient) {
      this.stompClient.send('/app/vehicle');
    }
  }

  loadDemo(name: string): void {
    if (this.stompClient) {
      this.stompClient.send(`/app/demo/${name}`);
    }
  }

  deleteLocation(locationId: number) {
    if (this.stompClient) {
      this.stompClient.send(`/app/location/${locationId}/delete`, JSON.stringify(locationId)); // TODO no body
    }
  }

  deleteAnyVehicle() {
    if (this.stompClient) {
      this.stompClient.send('/app/vehicle/deleteAny');
    }
  }

  deleteVehicle(vehicleId: number) {
    if (this.stompClient) {
      this.stompClient.send(`/app/vehicle/${vehicleId}/delete`, JSON.stringify(vehicleId)); // TODO no body
    }
  }

  changeVehicleCapacity(vehicleId: number, capacity: number) {
    if (this.stompClient) {
      this.stompClient.send(`/app/vehicle/${vehicleId}/capacity`, JSON.stringify(capacity));
    }
  }

  clear() {
    if (this.stompClient) {
      this.stompClient.send('/app/clear');
    }
  }

  subscribeToServerInfo(subscriptionCallback: (serverInfo: ServerInfo) => any): void {
    if (this.stompClient) {
      this.stompClient.subscribe('/topic/serverInfo', (message) => {
        const serverInfo = JSON.parse(message.body);
        subscriptionCallback(serverInfo);
      });
    }
  }

  subscribeToRoute(subscriptionCallback: (plan: RoutingPlan) => any): void {
    if (this.stompClient) {
      this.stompClient.subscribe('/topic/route', (message) => {
        const plan = JSON.parse(message.body);
        subscriptionCallback(plan);
      });
    }
  }
}
