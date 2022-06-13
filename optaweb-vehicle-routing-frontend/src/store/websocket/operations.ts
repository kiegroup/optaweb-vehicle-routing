import { demoOperations } from '../demo';
import { FinishLoadingAction } from '../demo/types';
import { messageActions } from '../message';
import { MessageAction } from '../message/types';
import { routeOperations } from '../route';
import { UpdateRouteAction } from '../route/types';
import { serverOperations } from '../server';
import { ServerInfoAction } from '../server/types';
import { ThunkCommandFactory } from '../types';
import * as actions from './actions';
import { WebSocketAction } from './types';

type ConnectClientThunkAction =
  | WebSocketAction
  | MessageAction
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
        client.subscribeToErrorTopic((errorMessage) => {
          dispatch(messageActions.receiveMessage(errorMessage));
        });
        client.subscribeToRoute((plan) => {
          dispatch(routeOperations.updateRoute(plan));
          if (getState().demo.isLoading) {
            // TODO handle the case when serverInfo doesn't contain demo with the given name
            //      (that could only be possible due to a bug in the code)
            const demo = getState().serverInfo.demos.filter((value) => value.name === getState().demo.demoName)[0];
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
