import { Viewport } from 'react-leaflet';
import { ActionFactory } from '../types';
import { ActionType, ResetViewportAction, UpdateViewportAction } from './types';

export const updateViewport: ActionFactory<Viewport, UpdateViewportAction> = (viewport) => ({
  type: ActionType.UPDATE_VIEWPORT,
  value: viewport,
});

export const resetViewport: ActionFactory<void, ResetViewportAction> = () => ({
  type: ActionType.RESET_VIEWPORT,
});
