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

import { Action, ActionCreator, Dispatch } from 'redux';
import { ThunkAction } from 'redux-thunk';
import * as SockJS from 'sockjs-client';
import webstomp, { Client } from 'webstomp-client';
import { IAppState } from './configStore';
import TspActions, {
  IAddLocationAction,
  IClearSolutionAction,
  IDeleteLocationAction,
  ILoadDemoAction,
  IUpdateTSPSolutionAction,
} from './tsp/actions';
import { ILatLng } from './tsp/types';
import WebSocketActions, { WebSocketAction } from './websocket/actions';

/**
 * This dispatch is intended for async actions only. Those are connection status changes and
 * solution updates.
 */
type WebSocketDispatch = Dispatch<WebSocketAction | IUpdateTSPSolutionAction>;
/**
 * ThunkCommand is a ThunkAction that has no result (it's typically something like
 * `Promise<ActionAfterDataFetched>`, but sending messages over WebSocket usually has no response
 * (with the exception of subscribe), so most of our operations are void).
 *
 * @template A Type of action(s) allowed to be dispatched.
 */
type ThunkCommand<A extends Action> = ThunkAction<void, IAppState, undefined, A>;

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

let webSocket: WebSocket;
let stompClient: Client;

/**
 * Map dispatch function to socket events
 *
 * @param {Dispatch} dispatch
 */
const mapDispatchToEvents = (dispatch: Dispatch<IUpdateTSPSolutionAction>): void => {
  stompClient.subscribe('/topic/route', (message) => {
    const tsp = JSON.parse(message.body);
    dispatch(updateTSPSolution(tsp));
  });
};

/**
 * Connect TSP module to the websocket and use dispatch function issue
 * action about TSP
 *
 * @param {Dispatch} dispatch
 * @param {string} socketUrl
 */
const connectWs = (dispatch: WebSocketDispatch, socketUrl: string): void => {
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
      setTimeout(() => connectWs(dispatch, socketUrl), 1000);
    },
  );
};

const addLocationOp: ActionCreator<ThunkCommand<IAddLocationAction>> = (
  location: ILatLng,
) => (
  dispatch,
) => {
  dispatch(addLocation(location));
  stompClient.send('/app/place', JSON.stringify(location));
};

const loadDemoOp: ActionCreator<ThunkCommand<ILoadDemoAction>> = () => (dispatch) => {
  dispatch(loadDemo());
  stompClient.send('/app/demo');
};

const deleteLocationOp: ActionCreator<ThunkCommand<IDeleteLocationAction>> = (
  locationId: number,
) => (
  dispatch,
) => {
  dispatch(deleteLocation(locationId));
  stompClient.send(`/app/place/${locationId}/delete`, JSON.stringify(locationId));
};

const clearSolutionOp: ActionCreator<ThunkCommand<IClearSolutionAction>> = () => (dispatch) => {
  dispatch(clearSolution());
  stompClient.send('/app/clear');
};

export default {
  addLocation: addLocationOp,
  clearSolution: clearSolutionOp,
  connect: connectWs,
  deleteLocation: deleteLocationOp,
  loadDemo: loadDemoOp,
};
