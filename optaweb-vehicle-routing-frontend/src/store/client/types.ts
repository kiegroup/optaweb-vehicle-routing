import { Viewport as LeafletViewport } from 'react-leaflet';
import { Action } from 'redux';

export enum ActionType {
  UPDATE_VIEWPORT = 'UPDATE_VIEWPORT',
  RESET_VIEWPORT = 'RESET_VIEWPORT',
}

export interface UpdateViewportAction extends Action<ActionType.UPDATE_VIEWPORT> {
  value: LeafletViewport;
}

export interface ResetViewportAction extends Action<ActionType.RESET_VIEWPORT> {
}

export interface UserViewport {
  isDirty: boolean;
  center: [number, number];
  zoom: number;
}

export type ViewportAction =
  | UpdateViewportAction
  | ResetViewportAction;
