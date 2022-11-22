import { shallow, toJson } from 'ui/shallow-test-util';
import RouteMap, { Props } from './RouteMap';

describe('Route Map', () => {
  it('should show the whole world when bounding box is null', () => {
    const props: Props = {
      updateViewport: jest.fn,
      clickHandler: jest.fn(),
      removeHandler: jest.fn(),
      selectedId: 1,
      depot: {
        id: 1,
        lat: 1.345678,
        lng: 1.345678,
      },
      visits: [],
      routes: [{
        visits: [],
        track: [],
      }],
      boundingBox: null,
      userViewport: {
        isDirty: false,
        zoom: 4,
        center: [1, 1],
      },
    };
    const routeMap = shallow(<RouteMap {...props} />);
    expect(toJson(routeMap)).toMatchSnapshot();
  });

  it('should pan and zoom to show bounding box if viewport is not dirty', () => {
    const depot = {
      id: 1,
      lat: 1.345678,
      lng: 1.345678,
    };
    const visit2 = {
      id: 2,
      lat: 2.345678,
      lng: 2.345678,
    };
    const visit3 = {
      id: 3,
      lat: 3.676111,
      lng: 3.568333,
    };
    const props: Props = {
      updateViewport: jest.fn(),
      clickHandler: jest.fn(),
      removeHandler: jest.fn(),
      selectedId: 1,
      boundingBox: [{ lat: -1, lng: -2 }, { lat: 10, lng: 20 }],
      userViewport: {
        isDirty: false,
        zoom: 4,
        center: [1, 1],
      },
      depot,
      visits: [visit2, visit3],
      routes: [{
        visits: [visit2, visit3],
        track: [[0.111222, 0.222333], [0.444555, 0.555666]],
      }],
    };
    const routeMap = shallow(<RouteMap {...props} />);
    expect(toJson(routeMap)).toMatchSnapshot();
  });

  it('should ignore bounds if viewport is dirty', () => {
    const depot = {
      id: 1,
      lat: 1.345678,
      lng: 1.345678,
    };
    const props: Props = {
      updateViewport: jest.fn(),
      clickHandler: jest.fn(),
      removeHandler: jest.fn(),
      selectedId: NaN,
      boundingBox: [{ lat: -1, lng: -2 }, { lat: 10, lng: 20 }],
      userViewport: {
        isDirty: true,
        zoom: 4,
        center: [1, 1],
      },
      depot,
      visits: [],
      routes: [],
    };
    const routeMap = shallow(<RouteMap {...props} />);
    // Map's bounds should be undefined
    expect(toJson(routeMap)).toMatchSnapshot();
  });
});
