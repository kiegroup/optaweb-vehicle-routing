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

import SockJS from "sockjs-client";
import webstomp, { Client, Frame } from "webstomp-client";
import Creators, {
  AddLocationAction,
  DeleteLocationAction,
  InitWsConnectionAction,
  UpdateTSPSolutionAction,
  WsConnectionFailureAction,
  WsConnectionSuccessAction
} from "./actions";
import { Store, Dispatch } from "redux";
import { GPSLocation } from "./types";
const {
  addLocation,
  deleteLocation,
  updateTSPSolution,
  initWsConnection,
  wsConnectionSuccess,
  wsConnectionFailure
} = Creators;

let webSocket: WebSocket;
let stompClient: Client;

function mapEventToActions(dispatch: Dispatch) {
  stompClient.subscribe("/topic/route", message => {
    const tsp = JSON.parse(message.body);
    dispatch(updateTSPSolution(tsp));
  });
}

function connectWs(store: Store, socketUrl: string) {
  const { dispatch } = store;
  return () => {
    webSocket = new SockJS(socketUrl);
    stompClient = webstomp.over(webSocket, { debug: true });

    dispatch(initWsConnection(socketUrl));
    stompClient.connect(
      {}, // no headers
      () => {
        // on connection, subscribe to the route topic
        dispatch(wsConnectionSuccess(stompClient));
        mapEventToActions(dispatch);
      },
      err => {
        // on error, schedule a reconnection attempt
        dispatch(wsConnectionFailure(err));
        setTimeout(() => connectWs(store, socketUrl)(), 1000);
      }
    );
  };
}

const addLocationOp = (location: GPSLocation) => (dispatch: Dispatch) => {
  if (!location) {
    return;
  }

  stompClient.send("/app/place", JSON.stringify(location));
  dispatch(addLocation(location));
};

const loadDemoOp = () => {
  stompClient.send("/app/demo");
  return addLocation();
};

const deleteLocationOp = (locationId: number) => (dispatch: Dispatch) => {
  if (!locationId) {
    return;
  }
  stompClient.send(
    `/app/place/${locationId}/delete`,
    JSON.stringify(locationId)
  );
  dispatch(deleteLocation(locationId));
};

export default {
  connect: connectWs,
  addLocation: addLocationOp,
  loadDemo: loadDemoOp,
  deleteLocation: deleteLocationOp
};
