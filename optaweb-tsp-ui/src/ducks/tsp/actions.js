

import types from './types';

function makeActionCreator(type, ...argNames) {
  return function actionCreator(...args) {
    const action = { type };
    argNames.forEach((arg, index) => {
      action[argNames[index]] = args[index];
    });
    return action;
  };
}


// consider https://github.com/infinitered/reduxsauce  for DRY approach

const actions = {
  addLocation: makeActionCreator(types.ADD_LOCATION, 'value'),

  deleteLocation: makeActionCreator(types.DELETE_LOCATION, 'value'),

  updateTPSSolution: makeActionCreator(types.SOLUTION_UPDATES_DATA, 'solution'),


  initWsConnection: makeActionCreator(types.WS_CONNECT, 'value'),
  wsConnectionSuccess: makeActionCreator(types.WS_CONNECT_SUCCESS, 'value'),
  wsConnectionFailure: makeActionCreator(types.WS_CONNECT_FAILURE, 'value'),
};


export default actions;
