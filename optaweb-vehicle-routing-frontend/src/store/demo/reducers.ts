import { ActionType, Demo, DemoAction } from './types';

const initialState: Demo = {
  isLoading: false,
  demoName: null,
};

export const demoReducer = (state = initialState, action: DemoAction): Demo => {
  switch (action.type) {
    case ActionType.REQUEST_DEMO: {
      return { ...initialState, isLoading: true, demoName: action.name };
    }
    case ActionType.FINISH_LOADING: {
      return { ...state, isLoading: false };
    }
    default:
      return state;
  }
};
