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
import { ILatLng, IRoutingPlan } from 'store/route/types';
import webstomp, { Client, Frame } from 'webstomp-client';

export default class WebSocketClient {

  readonly socketUrl: string;
  webSocket: WebSocket;
  stompClient: Client;

  constructor(socketUrl: string) {
    this.socketUrl = socketUrl;
  }

  connect(successCallback: () => any, errorCallback: (err: CloseEvent | Frame) => any) {
    this.webSocket = new SockJS(this.socketUrl);
    this.stompClient = webstomp.over(this.webSocket, { debug: true });
    this.stompClient.connect(
      {}, // no headers
      () => {
        successCallback();
      },
      (err) => {
        errorCallback(err);
      },
    );
  }

  addLocation(latLng: ILatLng) {
    this.stompClient.send('/app/location', JSON.stringify(latLng));
  }

  loadDemo(callback: (demoSize: number) => any): void {
    const subscription = this.stompClient.subscribe('/topic/demo', (message) => {
      subscription.unsubscribe();
      const demoSize: number = JSON.parse(message.body);
      callback(demoSize);
    });
    this.stompClient.send('/app/demo');
  }

  deleteLocation(locationId: number) {
    this.stompClient.send(`/app/location/${locationId}/delete`, JSON.stringify(locationId));
  }

  clear() {
    this.stompClient.send('/app/clear');
  }

  subscribe(subscriptionCallback: (plan: IRoutingPlan) => any): void {
    this.stompClient.subscribe('/topic/route', (message) => {
      const plan = JSON.parse(message.body);
      subscriptionCallback(plan);
    });
  }
}
