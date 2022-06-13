import { Viewport } from 'react-leaflet';
import * as actions from './actions';
import reducer from './index';
import { initialViewportState } from './reducers';
import { UserViewport } from './types';

const zoom = 13;
const center: [number, number] = [1, -3];
const userViewport: UserViewport = {
  isDirty: true,
  zoom,
  center,
};

describe('Client reducer', () => {
  it('update viewport', () => {
    expect(
      reducer(initialViewportState, actions.updateViewport({ zoom, center })),
    ).toEqual(userViewport);
    expect(
      reducer(initialViewportState, actions.updateViewport({ zoom: null, center })),
    ).toEqual(initialViewportState);
    expect(
      reducer(initialViewportState, actions.updateViewport({ zoom, center: null })),
    ).toEqual(initialViewportState);
    expect(
      reducer(initialViewportState, actions.updateViewport(null as unknown as Viewport)),
    ).toEqual(initialViewportState);
  });

  it('reset viewport', () => {
    expect(
      reducer(userViewport, actions.resetViewport()),
    ).toEqual(initialViewportState);
  });
});
