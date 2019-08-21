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

import { demoOperations } from '../demo';
import { FinishLoadingAction } from '../demo/types';
import { routeOperations } from '../route';
import { UpdateRouteAction } from '../route/types';
import { serverOperations } from '../server';
import { ServerInfoAction } from '../server/types';
import { ThunkCommandFactory } from '../types';
import * as actions from './actions';
import { WebSocketAction } from './types';

type ConnectClientThunkAction =
  | WebSocketAction
  | UpdateRouteAction
  | FinishLoadingAction
  | ServerInfoAction;

/**
 * Connect the client to WebSocket.
 */
export const connectClient: ThunkCommandFactory<void, ConnectClientThunkAction> = (
  () => (dispatch, getState, client) => {
    // dispatch WS connection initializing
    dispatch(actions.initWsConnection());
    client.connect(
      // on connection, subscribe to the route topic
      () => {
        dispatch(actions.wsConnectionSuccess());
        client.subscribeToServerInfo((serverInfo) => {
          dispatch(serverOperations.serverInfo(serverInfo));
        });
        client.subscribeToRoute((plan) => {
          dispatch(routeOperations.updateRoute(plan));
          if (getState().demo.isLoading) {
            // TODO handle the case when serverInfo doesn't contain demo with the given name
            //      (that could only be possible due to a bug in the code)
            const demo = getState().serverInfo.demos.filter(value => value.name === getState().demo.demoName)[0];
            if (plan.visits.length === demo.visits) {
              dispatch(demoOperations.finishLoading());
            }
          }
        });
      },
      // on error, schedule a reconnection attempt
      (err) => {
        // TODO try to pass the original err object or test it here and
        //      dispatch different actions based on its properties (Frame vs. CloseEvent, reason etc.)
        dispatch(actions.wsConnectionFailure(JSON.stringify(err)));
        setTimeout(() => dispatch(connectClient()), 1000);
      },
    );
  });
