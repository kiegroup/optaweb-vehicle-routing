import { Action } from 'redux';
import { LatLng } from '../route/types';

export enum ActionType {
  SERVER_INFO = 'SERVER_INFO',
}

export interface ServerInfoAction extends Action<ActionType.SERVER_INFO> {
  value: ServerInfo;
}

export interface Demo {
  name: string;
  visits: number;
}

export interface ServerInfo {
  boundingBox: [LatLng, LatLng] | null;
  countryCodes: string[];
  demos: Demo[];
}
