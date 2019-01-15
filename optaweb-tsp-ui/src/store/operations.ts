/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import { Dispatch } from 'redux';
import * as SockJS from 'sockjs-client';
import webstomp, { Client } from 'webstomp-client';
import TspActions from './tsp/actions';
import { ILatLng } from './tsp/types';
import WebSocketActions from './websocket/actions';

const {
  addLocation,
  clearSolution,
  deleteLocation,
  loadDemo,
  updateTSPSolution,
} = TspActions;

const {
  initWsConnection,
  wsConnectionSuccess,
  wsConnectionFailure,
} = WebSocketActions;

interface IWSConnectionOpts {
  socketUrl: string;
}

interface ITSPConfig extends IWSConnectionOpts {
  dispatch: Dispatch;
}

let webSocket: WebSocket;
let stompClient: Client;

/**
 * Map dispatch function to socket events
 *
 * @param {Dispatch} dispatch
 */
function mapDispatchToEvents(dispatch: Dispatch) {
  stompClient.subscribe('/topic/route', (message) => {
    const tsp = JSON.parse(message.body);
    dispatch(updateTSPSolution(tsp));
  });
}

/**
 * Connect TSP module to the websocket and use dispatch function issue
 * action about TSP
 *
 * @param {Dispatch} dispatch
 * @param {string} socketUrl
 */
function connectWs({ dispatch, socketUrl }: ITSPConfig): void {
  webSocket = new SockJS(socketUrl);
  stompClient = webstomp.over(webSocket, { debug: true });

  // dispatch WS connection initializing
  dispatch(initWsConnection(socketUrl));
  stompClient.connect(
    {}, // no headers
    () => {
      // on connection, subscribe to the route topic
      dispatch(wsConnectionSuccess(stompClient));
      mapDispatchToEvents(dispatch);
    },
    (err) => {
      // on error, schedule a reconnection attempt
      dispatch(wsConnectionFailure(err));
      setTimeout(() => connectWs({ dispatch, socketUrl }), 1000);
    },
  );
}

const addLocationOp = (location: ILatLng) => {
  stompClient.send('/app/place', JSON.stringify(location));
  return addLocation(location);
};

const loadDemoOp = () => {
  stompClient.send('/app/demo');
  return loadDemo();
};

const deleteLocationOp = (locationId: number) => {
  stompClient.send(
    `/app/place/${locationId}/delete`,
    JSON.stringify(locationId),
  );
  return deleteLocation(locationId);
};

const clearSolutionOp = () => {
  stompClient.send('/app/clear');
  return clearSolution();
};

export default {
  addLocation: addLocationOp,
  clearSolution: clearSolutionOp,
  connect: connectWs,
  deleteLocation: deleteLocationOp,
  loadDemo: loadDemoOp,
};
