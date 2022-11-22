import { ActionType, UserViewport, ViewportAction } from './types';

export const initialViewportState: UserViewport = {
  isDirty: false,
  center: [0, 0],
  zoom: 2,
};

// eslint-disable-next-line @typescript-eslint/default-param-last
export const clientReducer = (state = initialViewportState, action: ViewportAction): UserViewport => {
  switch (action.type) {
    case ActionType.UPDATE_VIEWPORT: {
      if (!action.value || !action.value.zoom || !action.value.center) {
        return state;
      }
      return { isDirty: true, zoom: action.value.zoom, center: action.value.center };
    }
    case ActionType.RESET_VIEWPORT: {
      return initialViewportState;
    }
    default:
      return state;
  }
};
