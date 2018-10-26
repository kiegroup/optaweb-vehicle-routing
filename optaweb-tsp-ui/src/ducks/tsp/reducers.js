

import types from './types';


const INITIAL_STATE = {
  route: [],
  domicileId: -1,
  distance: '0.00',
};


export default function tspReducer(state = INITIAL_STATE, action) {
  switch (action.type) {
    case types.SOLUTION_UPDATES_DATA: {
      const { route, distance } = action.solution;
      return {
        route,
        domicileId: route.length > 0 ? route[0].id : NaN,
        distance,
      };
    }
    case types.ADD_LOCATION:
    case types.ADD_DEMO_LOCATION:
    default: return state;
  }
}

