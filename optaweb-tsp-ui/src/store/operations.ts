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
import TspClient from '../websocket/TspClient';
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

let client: TspClient;

/**
 * Map dispatch function to socket events
 *
 * @param {Dispatch} dispatch
 */
const mapDispatchToEvents = (dispatch: Dispatch<IUpdateTSPSolutionAction>): void => {
  client.subscribe(route => dispatch(updateTSPSolution(route)));
};

/**
 * Connect TSP module to the websocket and use dispatch function issue
 * action about TSP
 *
 * @param tspWsClient
 */
const connectWs: ActionCreator<ThunkCommand<WebSocketAction | IUpdateTSPSolutionAction>> = (
  tspWsClient: TspClient,
) => (dispatch) => {
  client = tspWsClient;

  // dispatch WS connection initializing
  dispatch(initWsConnection());
  client.connect(
    () => {
      // on connection, subscribe to the route topic
      dispatch(wsConnectionSuccess());
      mapDispatchToEvents(dispatch);
    },
    (err) => {
      // on error, schedule a reconnection attempt
      dispatch(wsConnectionFailure(err));
      setTimeout(() => dispatch(connectWs(tspWsClient)), 1000);
    });
};

const addLocationOp: ActionCreator<ThunkCommand<IAddLocationAction>> = (
  location: ILatLng,
) => (
  dispatch,
) => {
  dispatch(addLocation(location));
  client.addLocation(location);
};

const loadDemoOp: ActionCreator<ThunkCommand<ILoadDemoAction>> = () => (dispatch) => {
  dispatch(loadDemo());
  client.loadDemo();
};

const deleteLocationOp: ActionCreator<ThunkCommand<IDeleteLocationAction>> = (
  locationId: number,
) => (
  dispatch,
) => {
  dispatch(deleteLocation(locationId));
  client.deleteLocation(locationId);
};

const clearSolutionOp: ActionCreator<ThunkCommand<IClearSolutionAction>> = () => (dispatch) => {
  dispatch(clearSolution());
  client.clear();
};

export default {
  addLocation: addLocationOp,
  clearSolution: clearSolutionOp,
  connect: connectWs,
  deleteLocation: deleteLocationOp,
  loadDemo: loadDemoOp,
};
