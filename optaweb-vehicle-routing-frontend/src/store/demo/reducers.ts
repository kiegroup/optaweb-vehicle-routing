import { ActionType, Demo, DemoAction } from './types';

const initialState: Demo = {
  isLoading: false,
  demoName: null,
};

// eslint-disable-next-line @typescript-eslint/default-param-last
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
