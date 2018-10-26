// operations.js
import SockJS from 'sockjs-client';
import webstomp from 'webstomp-client';
import Creators from './actions';

const {
  addLocation,
  deleteLocation,
  updateTPSSolution,
  initWsConnection,
  wsConnectionSuccess,
  wsConnectionFailure }
  = Creators;


let webSocket;
let stompClient;

function mapEventToActions(dispatch) {
  stompClient.subscribe('/topic/route', (message) => {
    const tsp = JSON.parse(message.body);
    dispatch(updateTPSSolution(tsp));
  });
}

function connectWs(store, socketUrl) {
  return (dispatch) => {
    webSocket = new SockJS(socketUrl);
    stompClient = webstomp.over(webSocket, { debug: true });

    dispatch(initWsConnection(socketUrl));
    stompClient.connect(
      {}, // no headers
      () => { // on connection, subscribe to the route topic
        dispatch(wsConnectionSuccess(stompClient));
        mapEventToActions(store.dispatch);
      }, (err) => { // on error, schedule a reconnection attempt
        dispatch(wsConnectionFailure(err));
        setTimeout(() => connectWs(store, socketUrl), 1000);
      });
  };
}


const addLocationOp = (location, demo = false) => (dispatch) => {
  if (demo) {
    stompClient.send('/app/demo');
    dispatch(addLocation(location));
    return;
  }
  if (!location) {
    return;
  }

  stompClient.send('/app/place', JSON.stringify(location));
  dispatch(addLocation(location));
};

const deleteLocationOp = locationId => (dispatch) => {
  if (!locationId) {
    return;
  }
  stompClient.send(`/app/place/${locationId}/delete`, JSON.stringify(locationId));
  dispatch(deleteLocation(locationId));
};

export default {
  connect: connectWs,
  addLocation: addLocationOp,
  deleteLocation: deleteLocationOp,

};

